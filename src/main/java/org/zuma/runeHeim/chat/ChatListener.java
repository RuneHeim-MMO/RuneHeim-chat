package org.zuma.runeHeim.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class ChatListener implements Listener {
    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        event.renderer((source, displayName, message, viewer) ->
                Component.text()
                        .append(Component.text("[VIP] ", NamedTextColor.GOLD))
                        .append(displayName)
                        .append(Component.text(": ", NamedTextColor.WHITE))
                        .append(message.color(NamedTextColor.YELLOW))
                        .build()
        );
    }
}
