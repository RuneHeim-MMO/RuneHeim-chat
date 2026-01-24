package org.zuma.runeHeim.chat.base;

import org.zuma.examplePlugin.domain.player.RunePlayer;

import java.util.UUID;

public record PlayerData(String name, PlayerPrefix prefix, UUID uuid, int level, String baseRole) {
    public static PlayerData fromPlayerData(RunePlayer player) {
        return new PlayerData(player.asBukkitPlayer().getName(), PlayerPrefix.getPrefix(player), player.getUUID(), player.getCombatLvl(), player.getRole().getBaseRole().name());
    }
}
