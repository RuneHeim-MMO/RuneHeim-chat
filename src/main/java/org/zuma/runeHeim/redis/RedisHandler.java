package org.zuma.runeHeim.redis;

import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.redis.GlobalEventHandler;
import org.zuma.runeHeim.chat.ChatHandler;
import org.zuma.runeHeim.events.RuneChatEvent;
import org.zuma.runeHeim.events.RuneChatGlobalBroadcasterStateUpdate;

public class RedisHandler {

    public static void registerEvents() {
        GlobalEventHandler.registerEvent(RuneChatGlobalBroadcasterStateUpdate.class, RuneChatGlobalBroadcasterStateUpdate.Payload.class);
        GlobalEventHandler.registerEvent(RuneChatEvent.class, RuneChatEvent.Payload.class);

    }

    public static void startGlobalEventListener() {
        GlobalEventHandler.addListener(RuneChatGlobalBroadcasterStateUpdate.class, payload -> {
            ChatHandler.setSendGlobalChatEvents(payload.sendGlobalEvents);
        });
    }
}
