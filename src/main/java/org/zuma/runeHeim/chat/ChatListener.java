package org.zuma.runeHeim.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.logger.constants.LogLevel;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.util.HudUtils;

import java.util.Map;

public class ChatListener implements Listener {
    @EventHandler
    public void onAsyncChat(AsyncChatEvent event) {
        Player player = event.getPlayer();
        RunePlayer runePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(player);

        if (runePlayer == null) {
            event.setCancelled(true);
            return;
        }

        String originalMessage = PlainTextComponentSerializer.plainText().serialize(event.message());
        Map.Entry<String, Boolean> profanityResult = ProfanityFilter.censorBadWords(originalMessage);
        Component censoredMessage = Component.text(profanityResult.getKey(), TextColor.color(191, 191, 191));

        if (profanityResult.getValue()) {
            Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                HudUtils.showProfanityWarning(runePlayer);
                StrikeHandler.addStrike(runePlayer.getUUID());
                RuneQuestPlugin.getRuneLogger().log(LogLevel.WARN, "Profanity detected in " + player.getName() + "'s message. Original message: " + player.getName() + ": '" + originalMessage + "'");
            });
        }

        event.renderer((source, originalName, message, viewer) -> {
            return Renderer.renderPlayerMessage(runePlayer, source, originalName, censoredMessage, viewer);
        });
    }

}
