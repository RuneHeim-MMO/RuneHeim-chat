package org.zuma.runeHeim.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.domain.player.RunePlayer;

public class ChatListener implements Listener {

    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player sender = event.getPlayer();
        RunePlayer senderRunePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(sender);

        if (senderRunePlayer == null) {
            event.setCancelled(true);
            return;
        }

        ChatHandler.handleChatEvent(event, sender, senderRunePlayer);

    }

}
