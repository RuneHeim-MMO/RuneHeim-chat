package org.zuma.runeHeim.chat.proximity;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.HashMap;
import java.util.UUID;

public class ProximityManager implements Listener {
    private final HashMap<UUID, BroadcastMode> broadcastMode;
    private final HashMap<UUID, SpyMode> spyMode;

    public ProximityManager() {
        this.broadcastMode = new HashMap<>();
        this.spyMode = new HashMap<>();
    }

    public BroadcastMode getBroadcastMode(UUID playerUUID) {
        return broadcastMode.getOrDefault(playerUUID, BroadcastMode.LOCAL);
    }

    public void setBroadcastMode(UUID playerUUID, BroadcastMode mode) {
        broadcastMode.put(playerUUID, mode);
    }

    public SpyMode getSpyMode(UUID playerUUID) {
        return spyMode.getOrDefault(playerUUID, SpyMode.LOCAL);
    }

    public void setSpyMode(UUID playerUUID, SpyMode mode) {
        spyMode.put(playerUUID, mode);
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        broadcastMode.remove(playerUUID);
        spyMode.remove(playerUUID);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        broadcastMode.putIfAbsent(playerUUID, BroadcastMode.LOCAL);
        spyMode.remove(playerUUID);
    }


}
