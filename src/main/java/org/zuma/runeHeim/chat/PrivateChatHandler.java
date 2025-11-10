package org.zuma.runeHeim.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.constants.Color;
import org.zuma.examplePlugin.core.permissions.PlayerRole;
import org.zuma.examplePlugin.core.utils.TextUtils;
import org.zuma.examplePlugin.domain.cooldown.CooldownHandler;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.chat.constants.Template;
import org.zuma.runeHeim.events.RunePrivateChatACKEvent;
import org.zuma.runeHeim.events.RunePrivateChatSYNEvent;
import org.zuma.runeHeim.util.PlayerUtils;

import javax.annotation.Nullable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PrivateChatHandler {
    // Key: Sender UUID, Value: List of (ReceiverName & message uuid key)
    public final static ConcurrentHashMap<UUID, List<Map.Entry<String, UUID>>> pendingAcks = new ConcurrentHashMap<>();
    private final static CooldownHandler cooldownHandler = new CooldownHandler();
    private final static HashMap<UUID, String> recentMessages = new HashMap<>(); // Player name : last private message sender name

    public static void clearPendingAcks(UUID playerUUID) {
        pendingAcks.remove(playerUUID);
    }

    public static void setRecentMessageSender(UUID playerUUID, String senderName) {
        recentMessages.put(playerUUID, senderName);
    }

    @Nullable
    public static String getRecentMessageSender(UUID playerUUID) {
        return recentMessages.get(playerUUID);
    }

    public static void sendPrivateMessage(Player sender, String receiverName, String content) {
        RunePlayer runePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(sender);

        if (runePlayer == null) {
            return;
        }

        if (!runePlayer.asBukkitPlayer().hasPermission("runeheim.chat.cooldown")) {
            if (cooldownHandler.isCooldownInProgress(runePlayer)) {
                sender.sendMessage(Component.text("You are sending messages too quickly.", Color.ERROR.asColor()));
                cooldownHandler.startCooldown(runePlayer, 2000L);
                return;
            }

            cooldownHandler.startCooldown(runePlayer, 2000L);
        }

        String censoredMessage = ChatHandler.handleProfanityCensoring(content, runePlayer, sender);
        Player localReceiverPlayer = PlayerUtils.findPlayer(receiverName);

        // Local receiver found, send them message directly without broadcasting events
        if (localReceiverPlayer != null && localReceiverPlayer.isOnline()) {
            RunePlayer localReceiverRunePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(localReceiverPlayer);
            if (localReceiverRunePlayer == null) return;

            localReceiverPlayer.sendMessage(Component.text()
                    .append(Component.text("[PM] ", TextColor.color(77, 128, 240)))
                    .append(runePlayer.getRole().getPrefix())
                    .append(Component.text(sender.getName(), NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(content))
                    .build());

            sender.sendMessage(Component.text()
                    .append(Component.text("[PM] ", TextColor.color(77, 128, 240)))
                    .append(Component.text("You", NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(localReceiverRunePlayer.getRole().getPrefix())
                    .append(Component.text(localReceiverPlayer.getName(), NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(content))
                    .build());

            setRecentMessageSender(localReceiverPlayer.getUniqueId(), sender.getName());

            // Notify console
            Bukkit.getConsoleSender().sendMessage(Component.text()
                    .append(Component.text("[PM] ", TextColor.color(77, 128, 240)))
                    .append(Component.text(sender.getName(), NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(localReceiverPlayer.getName(), NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(content))
                    .build());

            return;
        }

        Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
            UUID messageUUID = UUID.randomUUID();
            String senderPrefix = TextUtils.extractPlainText(runePlayer.getRole().getPrefix());
            RunePrivateChatSYNEvent chatEvent = new RunePrivateChatSYNEvent(new RunePrivateChatSYNEvent.Payload(sender.getName(), senderPrefix, sender.getUniqueId(), receiverName, censoredMessage, messageUUID));
            Bukkit.getServer().getPluginManager().callEvent(chatEvent);

            if (!pendingAcks.containsKey(sender.getUniqueId())) {
                pendingAcks.put(sender.getUniqueId(), new ArrayList<>());
            }

            pendingAcks.get(sender.getUniqueId()).add(new AbstractMap.SimpleEntry<>(receiverName, messageUUID));


            UUID originalSenderUUID = sender.getUniqueId();
            Bukkit.getScheduler().runTaskLater(RuneQuestPlugin.getInstance(), () -> {
                // Check pending ACKs for the original sender
                if (pendingAcks.containsKey(originalSenderUUID)) {
                    List<Map.Entry<String, UUID>> ackList = pendingAcks.get(originalSenderUUID);
                    boolean removed = ackList.removeIf(entry -> entry.getValue().equals(messageUUID));

                    // Only notify the original sender if the pending entry was still present (i.e. delivery failed)
                    if (removed) {
                        Player originalSender = Bukkit.getPlayer(originalSenderUUID);
                        if (originalSender != null && originalSender.isOnline()) {
                            originalSender.sendMessage(Component.text(receiverName + " is not online.", Color.ERROR.asColor()));
                        }
                    }
                }
            }, 20L);
        });
    }

    // SYN received, deliver message to receiver
    public static void onSYNPrivateChatEventReceived(int serverId, String senderName, String senderPrefix, String senderUUID, String receiverName, String content, UUID messageUUID) {
        Player receiverPlayer = PlayerUtils.findPlayer(receiverName);

        // Receiver found, send them message
        if (receiverPlayer != null && receiverPlayer.isOnline()) {
            RunePlayer receiverRunePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(receiverPlayer);
            if (receiverRunePlayer == null) return;

            receiverPlayer.sendMessage(Component.text()
                    .append(Template.PRIVATE_MESSAGE.component())
                    .append(Component.text("[#" + serverId + "] ", TextColor.color(255, 85, 0)))
                    .append(Component.text(senderPrefix))
                    .append(Component.text(senderName, NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(content))
                    .build());
            setRecentMessageSender(receiverPlayer.getUniqueId(), senderName);


            // Notify console
            Bukkit.getConsoleSender().sendMessage(Component.text()
                    .append(Template.PRIVATE_MESSAGE.component())
                    .append(Component.text("[#" + serverId + "] ", TextColor.color(255, 85, 0)))
                    .append(Component.text(senderName, NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(receiverName, NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(content))
                    .build());

            Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                String receiverPrefix = TextUtils.extractPlainText(receiverRunePlayer.getRole().getPrefix());

                RunePrivateChatACKEvent chatEvent = new RunePrivateChatACKEvent(new RunePrivateChatACKEvent.Payload(senderName, senderPrefix, UUID.fromString(senderUUID), receiverPlayer.getName(), receiverPrefix, content, messageUUID, serverId));
                Bukkit.getServer().getPluginManager().callEvent(chatEvent);
            });

        }
    }

    // ACK received, notify original sender
    public static void onACKPrivateChatEventReceived(int serverId, String originalMessageSender, String senderPrefix, String receiverName, String receiverPrefix, String content, UUID messageUUID) {
        Player receiverPlayer = Bukkit.getPlayerExact(originalMessageSender);

        // Sender found, replay original message after it was delivered
        if (receiverPlayer != null && receiverPlayer.isOnline()) {
            receiverPlayer.sendMessage(Component.text()
                    .append(Template.PRIVATE_MESSAGE.component())
                    .append(Component.text("You", NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text("[#" + serverId + "] ", TextColor.color(255, 85, 0)))
                    .append(Component.text(receiverPrefix))
                    .append(Component.text(receiverName, NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(content))
                    .build());

            // Notify console
            Bukkit.getConsoleSender().sendMessage(Component.text()
                    .append(Template.PRIVATE_MESSAGE.component())
                    .append(Component.text(originalMessageSender, NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text("[#" + serverId + "] ", TextColor.color(255, 85, 0)))
                    .append(Component.text(receiverName, NamedTextColor.WHITE))
                    .append(Template.SEPARATOR.component())
                    .append(Component.text(content))
                    .build());

            // Remove from pending ACKs
            if (pendingAcks.containsKey(receiverPlayer.getUniqueId())) {
                List<Map.Entry<String, UUID>> ackList = pendingAcks.get(receiverPlayer.getUniqueId());
                ackList.removeIf(entry -> entry.getValue().equals(messageUUID));
            }
        }
    }
}
