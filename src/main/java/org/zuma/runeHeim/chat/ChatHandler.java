package org.zuma.runeHeim.chat;

import io.papermc.paper.event.player.AsyncChatEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.logger.constants.LogLevel;
import org.zuma.examplePlugin.core.utils.MathUtils;
import org.zuma.examplePlugin.domain.hud.HudService;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.util.HudUtils;
import org.zuma.runeHeim.util.TextUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class ChatHandler {
    public final static int CHAT_DISTANCE_THRESHOLD = 255;

    public static void handleChatEvent(AsyncChatEvent event, Player sender, RunePlayer senderRunePlayer) {
        Map<UUID, Double> otherPlayerDistances = calculateViewerDistances(event, sender);
        removeTooFarAwayViewers(event, otherPlayerDistances);
        int visiblePlayers = getPlayerViewersCount(event, otherPlayerDistances, sender);

        if (visiblePlayers == 0) {
            Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                HudService.sendInfoMessage(senderRunePlayer, Component.text("You are too far away from other players. Nobody can hear you."));
            });
        }

        Component censoredMessage = handleProfanityCensoring(event.message(), senderRunePlayer, sender);

        event.renderer((source, originalName, message, viewer) -> {
            Double distance = 0.0;

            // Get precalculated distances
            if (viewer instanceof Player playerViewer) {
                distance = otherPlayerDistances.getOrDefault(playerViewer.getUniqueId(), 0.0);
            }

            // Distance colorizing
            Component finalMessage = censoredMessage.color(TextUtils.getDistanceColor(distance, CHAT_DISTANCE_THRESHOLD));

            // Console logging
            if (viewer instanceof ConsoleCommandSender) {
                finalMessage = finalMessage.append(Component.text(" (" + visiblePlayers + " player viewers)"));
            }

            return Renderer.renderPlayerMessage(senderRunePlayer, source, originalName, finalMessage, viewer);
        });
    }

    public static Component handleProfanityCensoring(Component originalMessage, RunePlayer sender, Player bukkitSender) {
        String plainMessage = PlainTextComponentSerializer.plainText().serialize(originalMessage);
        Map.Entry<String, Boolean> profanityResult = ProfanityFilter.censorBadWords(plainMessage);
        Component censoredMessage = Component.text(profanityResult.getKey());

        if (profanityResult.getValue()) {
            Bukkit.getScheduler().runTask(RuneHeimChat.getInstance(), () -> {
                HudUtils.showProfanityWarning(sender);
                StrikeHandler.addStrike(sender.getUUID());
                RuneQuestPlugin.getRuneLogger().log(LogLevel.WARN, "Profanity detected in " + bukkitSender.getName() + "'s message. Original message: " + bukkitSender.getName() + ": '" + plainMessage + "'");
            });
        }

        return censoredMessage;
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

    public static int getPlayerViewersCount(AsyncChatEvent event, Map<UUID, Double> distances, Player sender) {
        int visiblePlayers = 0;
        Iterator<Audience> it = event.viewers().iterator();
        while (it.hasNext()) {
            Audience viewer = it.next();
            if (viewer instanceof Player playerViewer) {
                double distance = distances.getOrDefault(playerViewer.getUniqueId(), 0.0);
                if (distance > CHAT_DISTANCE_THRESHOLD) {
                    it.remove(); // remove viewer beyond threshold
                    continue;
                }
                if (!playerViewer.getUniqueId().equals(sender.getUniqueId())) visiblePlayers++;
            }
        }

        return visiblePlayers;
    }
}
