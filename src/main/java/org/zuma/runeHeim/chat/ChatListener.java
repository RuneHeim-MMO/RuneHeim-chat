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
import org.zuma.runeHeim.events.RuneGlobalChatEvent;

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

}
