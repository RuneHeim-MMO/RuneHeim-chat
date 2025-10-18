package org.zuma.runeHeim.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.chat.constants.Template;
import org.zuma.runeHeim.chat.proximity.ProximityManager;
import org.zuma.runeHeim.chat.proximity.SpyMode;

public class ChatListener implements Listener {
    private final ProximityManager proximityManager;

    public ChatListener(ProximityManager proximityManager) {
        this.proximityManager = proximityManager;
    }

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        RunePlayer senderRunePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(sender);

        if (senderRunePlayer == null) {
            event.setCancelled(true);
            return;
        }

        ChatHandler.handleChatEvent(event, sender, senderRunePlayer, proximityManager);

    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PrivateChatHandler.clearPendingAcks(event.getPlayer().getUniqueId());
    }

    public static void onGlobalChatEventReceived(int serverId, String senderName, String content) {
        Component message = Component.text()
                .append(Template.GLOBAL_MESSAGE.component())
                .append(Component.text("[#" + serverId + "] ", TextColor.color(255, 85, 0)))
                .append(Component.text(senderName, NamedTextColor.WHITE))
                .append(Template.SEPARATOR.component())
                .append(Component.text(content))
                .build();

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendMessage(message);
        });

        Bukkit.getConsoleSender().sendMessage(message);
    }

    public static void onSpyEventReceived(int serverId, String senderName, String content, int viewersCount) {
        Bukkit.getOnlinePlayers().forEach(player -> {
            if (senderName.equalsIgnoreCase(player.getName())) return;

            SpyMode mode = RuneHeimChat.getProximityManager().getSpyMode(player.getUniqueId());

            if (mode == SpyMode.NETWORK) {
                player.sendMessage(Component.text()
                        .append(Component.text("§9[SPY §d(#" + serverId + ", " + viewersCount + " viewers)§9] "))
                        .append(Component.text(senderName, NamedTextColor.WHITE))
                        .append(Template.SEPARATOR.component())
                        .append(Component.text(content))
                        .build());
            }
        });
    }

}
