package org.zuma.runeHeim;

import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents;
import org.bukkit.plugin.java.JavaPlugin;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.logger.constants.LogLevel;
import org.zuma.examplePlugin.core.redis.GlobalEventHandler;
import org.zuma.runeHeim.chat.ChatListener;
import org.zuma.runeHeim.chat.ProfanityFilter;
import org.zuma.runeHeim.chat.proximity.ProximityManager;
import org.zuma.runeHeim.commands.ChatCommand;
import org.zuma.runeHeim.commands.MsgCommand;
import org.zuma.runeHeim.commands.ReplyCommand;
import org.zuma.runeHeim.redis.RedisHandler;

public final class RuneHeimChat extends JavaPlugin {
    private static RuneHeimChat pluginInstance;
    private ProximityManager proximityManager;

    public static ProximityManager getProximityManager() {
        return pluginInstance.proximityManager;
    }

    public static RuneHeimChat getInstance() {
        return pluginInstance;
    }

    @Override
    public void onEnable() {
        pluginInstance = this;
        ProfanityFilter.loadConfigs();

        this.proximityManager = new ProximityManager();
        ChatListener chatListener = new ChatListener(this.proximityManager);

        getServer().getPluginManager().registerEvents(chatListener, pluginInstance);
        getServer().getPluginManager().registerEvents(this.proximityManager, pluginInstance);

        RedisHandler.registerEvents();

        pluginInstance.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register("chat", new ChatCommand()));

        pluginInstance.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register("msg", new MsgCommand()));

        pluginInstance.getLifecycleManager().registerEventHandler(LifecycleEvents.COMMANDS,
                event -> event.registrar().register("r", new ReplyCommand()));


        RuneQuestPlugin.getRuneLogger().log(LogLevel.INFO, "Chat initialized.");
    }

    @Override
    public void onDisable() {
        GlobalEventHandler.clearHandlers();
        RuneQuestPlugin.getRuneLogger().log(LogLevel.INFO, "Chat disabled.");
    }
}
