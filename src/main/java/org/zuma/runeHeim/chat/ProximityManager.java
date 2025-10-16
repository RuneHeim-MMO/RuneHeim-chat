package org.zuma.runeHeim.chat;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.zuma.runeHeim.ProximityMode;

import java.util.HashMap;
import java.util.UUID;

public class ProximityManager implements Listener {
    private final HashMap<UUID, ProximityMode> modes;

    public ProximityManager() {
        this.modes = new HashMap<>();
    }

    public ProximityMode getMode(UUID playerUUID) {
        return modes.getOrDefault(playerUUID, ProximityMode.LOCAL);
    }

    public void setMode(UUID playerUUID, ProximityMode mode) {
        modes.put(playerUUID, mode);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        modes.remove(playerUUID);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        modes.putIfAbsent(playerUUID, ProximityMode.LOCAL);
    }


}
