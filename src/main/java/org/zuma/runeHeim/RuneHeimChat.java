package org.zuma.runeHeim;

import org.bukkit.plugin.java.JavaPlugin;
import org.zuma.runeHeim.chat.ChatListener;

public final class RuneHeimChat extends JavaPlugin {
    private static RuneHeimChat pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;

        ChatListener chatListener = new ChatListener();
        getServer().getPluginManager().registerEvents(chatListener, pluginInstance);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public static RuneHeimChat getInstance() {
        return pluginInstance;
    }
}
