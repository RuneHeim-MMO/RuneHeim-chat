package org.zuma.runeHeim.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.constants.Color;
import org.zuma.examplePlugin.core.logger.constants.LogLevel;
import org.zuma.examplePlugin.core.utils.MathUtils;
import org.zuma.examplePlugin.core.utils.TextUtils;
import org.zuma.examplePlugin.domain.cooldown.CooldownHandler;
import org.zuma.examplePlugin.domain.hud.HudService;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.chat.proximity.BroadcastMode;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.chat.proximity.ProximityManager;
import org.zuma.runeHeim.chat.proximity.SpyMode;
import org.zuma.runeHeim.events.RuneChatSpyEvent;
import org.zuma.runeHeim.events.RuneGlobalChatBroadcastEvent;
import org.zuma.runeHeim.util.HudUtils;
import org.zuma.runeHeim.util.ChatTextUtils;

import java.util.*;
import java.util.stream.Collectors;

public class ChatHandler {
    private final static CooldownHandler cooldownHandler = new CooldownHandler();
    public final static int CHAT_DISTANCE_THRESHOLD = 255;

    public static void handleChatEvent(AsyncChatEvent event, Player sender, RunePlayer senderRunePlayer, ProximityManager proximityManager) {
        if (!senderRunePlayer.asBukkitPlayer().hasPermission("runeheim.chat.cooldown")) {
            if (cooldownHandler.isCooldownInProgress(senderRunePlayer)) {
                sender.sendMessage(Component.text("You are sending messages too quickly.", Color.ERROR.asColor()));
                event.setCancelled(true);
                cooldownHandler.startCooldown(senderRunePlayer, 1500L);
                return;
            }

            cooldownHandler.startCooldown(senderRunePlayer, 1500L);
        }

        BroadcastMode senderBroadcastMode = proximityManager.getBroadcastMode(sender.getUniqueId());
        boolean isServerMessage = senderBroadcastMode == BroadcastMode.SERVER;

        Map<UUID, Double> otherPlayerDistances = calculateViewerDistances(event, sender);
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        String censoredMessage = handleProfanityCensoring(plainMessage, senderRunePlayer, sender);

        switch (senderBroadcastMode) {
            case NETWORK -> {
                Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                    String senderPrefix = TextUtils.extractPlainText(senderRunePlayer.getRole().getPrefix());

                    RuneGlobalChatBroadcastEvent chatEvent = new RuneGlobalChatBroadcastEvent(new RuneGlobalChatBroadcastEvent.Payload(sender.getName(), senderPrefix, senderRunePlayer.getRole().getSupporterRoleIcon() , sender.getUniqueId(), censoredMessage));
                    Bukkit.getServer().getPluginManager().callEvent(chatEvent);
                });
                event.setCancelled(true);
                return; // Handled elsewhere
            }
            case SERVER -> {
                event.viewers().addAll(Bukkit.getOnlinePlayers());
            }
            case LOCAL -> {
                // Remove viewers that are too far away from an event
                removeTooFarAwayViewers(event, otherPlayerDistances);

                Bukkit.getOnlinePlayers().forEach(player -> {
                    SpyMode mode = proximityManager.getSpyMode(player.getUniqueId());

                    if (mode == SpyMode.SERVER && !event.viewers().contains(player)) {
                        int distance = (int) MathUtils.getLocationsDistanceXZ(sender.getLocation(), player.getLocation());
                        player.sendMessage(Component.text("§9[SPY §d(" + distance + " blocks)§9] ").append(Renderer.renderPlayerMessage(senderRunePlayer, sender, sender.displayName(), Component.text(censoredMessage, NamedTextColor.WHITE), player, senderBroadcastMode)));
                    }
                });
            }
        }


        int receiversCount = getPlayerViewersCount(event, otherPlayerDistances, sender, senderBroadcastMode);

        if (isServerMessage || senderBroadcastMode == BroadcastMode.LOCAL) {
            Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                String senderPrefix = TextUtils.extractPlainText(senderRunePlayer.getRole().getPrefix());

                RuneChatSpyEvent chatEvent = new RuneChatSpyEvent(new RuneChatSpyEvent.Payload(sender.getName(), senderPrefix, senderRunePlayer.getRole().getSupporterRoleIcon(), sender.getUniqueId(), censoredMessage, receiversCount));
                Bukkit.getServer().getPluginManager().callEvent(chatEvent);
            });
        }

        if (receiversCount == 0) {
            Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                if (senderBroadcastMode == BroadcastMode.LOCAL) {
                    HudService.sendInfoMessage(senderRunePlayer, Component.text("You are too far away from other players. Nobody can hear you."));
                }
            });
        }

        event.renderer((source, originalName, message, viewer) -> {
            Double distance = 0.0;

            // Get precalculated distances
            if (viewer instanceof Player playerViewer) {
                distance = otherPlayerDistances.getOrDefault(playerViewer.getUniqueId(), 0.0);
            }

            // Distance colorizing
            Component finalMessage = Component.text(censoredMessage).color(!isServerMessage ? ChatTextUtils.getDistanceColor(distance, CHAT_DISTANCE_THRESHOLD) : NamedTextColor.WHITE);

            // Console logging
            if (viewer instanceof ConsoleCommandSender) {
                finalMessage = finalMessage.append(Component.text(" (" + receiversCount + " player viewers)"));
            }

            return Renderer.renderPlayerMessage(senderRunePlayer, source, originalName, finalMessage, viewer, senderBroadcastMode);
        });
    }


    public static String handleProfanityCensoring(String plainMessage, RunePlayer sender, Player bukkitSender) {
        if (bukkitSender.hasPermission("runeheim.chat.profanity")) {
            return plainMessage;
        }

        Map.Entry<String, Boolean> profanityResult = ProfanityFilter.censorBadWords(plainMessage);

        if (profanityResult.getValue()) {
            Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                HudUtils.showProfanityWarning(sender);
                StrikeHandler.addStrike(sender.getUUID());
                RuneQuestPlugin.getRuneLogger().log(LogLevel.WARN, "Profanity detected in " + bukkitSender.getName() + "'s message. Original message: " + bukkitSender.getName() + ": '" + plainMessage + "'");
            });
        }

        return profanityResult.getKey();
    }

    public static Map<UUID, Double> calculateViewerDistances(AsyncChatEvent event, Player sender) {
        return event.viewers().stream()
                .filter(viewer -> viewer instanceof Player playerViewer && !playerViewer.getUniqueId().equals(sender.getUniqueId()))
                .collect(Collectors.toMap(
                        viewer -> ((Player) viewer).getUniqueId(),
                        viewer -> MathUtils.getLocationsDistanceXZ(sender.getLocation(), ((Player) viewer).getLocation())
                ));
    }

    public static void removeTooFarAwayViewers(AsyncChatEvent event, Map<UUID, Double> distances) {
        event.viewers().removeIf(viewer -> {
            if (!(viewer instanceof Player playerViewer)) return false;
            Double distance = distances.getOrDefault(playerViewer.getUniqueId(), 0.0);
            return distance > CHAT_DISTANCE_THRESHOLD; // remove this viewer
        });
    }

    public static int getPlayerViewersCount(AsyncChatEvent event, Map<UUID, Double> distances, Player sender, BroadcastMode senderMode) {
        int visiblePlayers = 0;
        Iterator<Audience> it = event.viewers().iterator();
        while (it.hasNext()) {
            Audience viewer = it.next();
            if (viewer instanceof Player playerViewer) {
                // always skip counting the sender themself
                if (playerViewer.getUniqueId().equals(sender.getUniqueId())) continue;

                // For server-mode messages, don't remove viewers by distance — count everyone
                if (senderMode == BroadcastMode.SERVER) {
                    visiblePlayers++;
                    continue;
                }

                double distance = distances.getOrDefault(playerViewer.getUniqueId(), 0.0);
                if (distance > CHAT_DISTANCE_THRESHOLD) {
                    it.remove(); // remove viewer beyond threshold for local mode
                    continue;
                }

                visiblePlayers++;
            }
        }

        return visiblePlayers;
    }

}
