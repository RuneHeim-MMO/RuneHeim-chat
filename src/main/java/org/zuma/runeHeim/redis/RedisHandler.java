package org.zuma.runeHeim.redis;

import org.zuma.examplePlugin.core.redis.GlobalEventHandler;
import org.zuma.runeHeim.chat.ChatHandler;
import org.zuma.runeHeim.events.RuneGlobalChatEvent;

public class RedisHandler {

    public static void registerEvents() {
        GlobalEventHandler.registerEvent(RuneGlobalChatEvent.class, RuneGlobalChatEvent.Payload.class);

        GlobalEventHandler.addListener(RuneGlobalChatEvent.class, payload -> {
            ChatHandler.onGlobalChat(payload.serverId, payload.senderName, payload.content);
        });
    }
}
