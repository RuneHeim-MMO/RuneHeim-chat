package org.zuma.runeHeim;

import org.bukkit.plugin.java.JavaPlugin;
import org.zuma.examplePlugin.core.logger.constants.LogLevel;
import org.zuma.runeHeim.chat.ChatListener;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.runeHeim.chat.ProfanityFilter;

public final class RuneHeimChat extends JavaPlugin {
    private static RuneHeimChat pluginInstance;

    @Override
    public void onEnable() {
        pluginInstance = this;
        ProfanityFilter.loadConfigs();

        ChatListener chatListener = new ChatListener();
        getServer().getPluginManager().registerEvents(chatListener, pluginInstance);

        RuneQuestPlugin.getRuneLogger().log(LogLevel.INFO, "Chat initialized.");

    }

    @Override
    public void onDisable() {
        RuneQuestPlugin.getRuneLogger().log(LogLevel.INFO, "Chat disabled.");
    }

    public static RuneHeimChat getInstance() {
        return pluginInstance;
    }
}
