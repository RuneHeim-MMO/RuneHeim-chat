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
}
