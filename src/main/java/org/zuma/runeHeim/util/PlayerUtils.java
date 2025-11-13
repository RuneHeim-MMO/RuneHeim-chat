package org.zuma.runeHeim.util;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PlayerUtils {
    public static Player getPlayerStartingWith(String receiverName) {
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().toLowerCase().startsWith(receiverName.toLowerCase())) {
                return player;
            }
        }
        return null;
    }

    public static Player findPlayer(String playerName) {
        // Try exact match first
        Player player = Bukkit.getPlayerExact(playerName);
        if (player != null && player.isOnline()) return player;

        // Fallback to partial match
        return PlayerUtils.getPlayerStartingWith(playerName);
    }
}
