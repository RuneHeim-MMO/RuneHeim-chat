package org.zuma.runeHeim.chat;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.core.constants.Color;
import org.zuma.examplePlugin.core.utils.TextUtils;

import java.util.HashMap;
import java.util.UUID;

public class StrikeHandler {
    public static HashMap<UUID, Integer> playerStrikes = new HashMap<>();
    public static int STRIKE_THRESHOLD = 3;

    public static void addStrike(UUID uuid) {
        playerStrikes.merge(uuid, 1, Integer::sum);

        if (playerStrikes.get(uuid) % STRIKE_THRESHOLD == 0) {
            Player player = Bukkit.getPlayer(uuid);

            if (player != null) {
                player.kick(TextUtils.getBrandKickScreen(
                        Component.text()
                                .append(Component.text("Profanity is not tolerated here!\n\nFurther offenses will result in a ", Color.ERROR.asColor()))
                                .append(Component.text("permanent ban.", Color.ERROR_LIGHT.asColor())
                                        .decorate(TextDecoration.UNDERLINED)).build())
                );
            }
        }
    }

    public static Integer getPlayerStrikes(UUID uuid) {
        return playerStrikes.get(uuid) == null ? 0 : playerStrikes.get(uuid);
    }

    public static Integer getThreshold() {
        return STRIKE_THRESHOLD;
    }
}
