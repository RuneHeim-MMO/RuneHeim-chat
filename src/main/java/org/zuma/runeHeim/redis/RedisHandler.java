package org.zuma.runeHeim.redis;

import org.zuma.examplePlugin.core.redis.GlobalEventHandler;
import org.zuma.runeHeim.chat.ChatHandler;
import org.zuma.runeHeim.chat.ChatListener;
import org.zuma.runeHeim.chat.PrivateChatHandler;
import org.zuma.runeHeim.events.RuneChatSpyEvent;
import org.zuma.runeHeim.events.RuneGlobalChatBroadcastEvent;
import org.zuma.runeHeim.events.RunePrivateChatACKEvent;
import org.zuma.runeHeim.events.RunePrivateChatSYNEvent;

public class RedisHandler {

    public static void registerEvents() {
        GlobalEventHandler.registerEvent(RuneChatSpyEvent.class, RuneChatSpyEvent.Payload.class);
        GlobalEventHandler.registerEvent(RuneGlobalChatBroadcastEvent.class, RuneGlobalChatBroadcastEvent.Payload.class);
        GlobalEventHandler.registerEvent(RunePrivateChatACKEvent.class, RunePrivateChatACKEvent.Payload.class);
        GlobalEventHandler.registerEvent(RunePrivateChatSYNEvent.class, RunePrivateChatSYNEvent.Payload.class);

        GlobalEventHandler.addListener(RuneChatSpyEvent.class, payload -> {
            ChatListener.onSpyEventReceived(payload.serverId, payload.senderName, payload.senderRank, payload.content, payload.viewers);
        });

        GlobalEventHandler.addListener(RuneGlobalChatBroadcastEvent.class, payload -> {
            ChatListener.onGlobalChatEventReceived(payload.serverId, payload.senderName, payload.senderRank, payload.content);
        });

        GlobalEventHandler.addListener(RunePrivateChatSYNEvent.class, payload -> {
            PrivateChatHandler.onSYNPrivateChatEventReceived(payload.serverId, payload.senderName, payload.senderRank, payload.senderUUID.toString(), payload.receiverName, payload.content, payload.messageUUID);
        });

        GlobalEventHandler.addListener(RunePrivateChatACKEvent.class, payload -> {
            PrivateChatHandler.onACKPrivateChatEventReceived(payload.serverId, payload.senderName, payload.senderRank, payload.receiverName, payload.receiverRank, payload.content, payload.messageUUID);
        });
    }
}
