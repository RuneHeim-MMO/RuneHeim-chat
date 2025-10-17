package org.zuma.runeHeim.redis;

import org.zuma.examplePlugin.core.redis.GlobalEventHandler;
import org.zuma.runeHeim.chat.ChatHandler;
import org.zuma.runeHeim.chat.PrivateChatHandler;
import org.zuma.runeHeim.events.RuneGlobalChatEvent;
import org.zuma.runeHeim.events.RunePrivateChatACKEvent;
import org.zuma.runeHeim.events.RunePrivateChatSYNEvent;

public class RedisHandler {

    public static void registerEvents() {
        GlobalEventHandler.registerEvent(RuneGlobalChatEvent.class, RuneGlobalChatEvent.Payload.class);
        GlobalEventHandler.registerEvent(RunePrivateChatACKEvent.class, RunePrivateChatACKEvent.Payload.class);
        GlobalEventHandler.registerEvent(RunePrivateChatSYNEvent.class, RunePrivateChatSYNEvent.Payload.class);

        GlobalEventHandler.addListener(RuneGlobalChatEvent.class, payload -> {
            ChatHandler.onGlobalChat(payload.serverId, payload.senderName, payload.content);
        });

        GlobalEventHandler.addListener(RunePrivateChatSYNEvent.class, payload -> {
            PrivateChatHandler.handleSYNPrivateChatEvent(payload.serverId, payload.senderName, payload.senderUUID.toString(), payload.receiverName, payload.content, payload.messageUUID);
        });

        GlobalEventHandler.addListener(RunePrivateChatACKEvent.class, payload -> {
            PrivateChatHandler.handleACKPrivateChatEvent(payload.serverId, payload.senderName, payload.receiverName, payload.content, payload.messageUUID);
        });
    }
}
