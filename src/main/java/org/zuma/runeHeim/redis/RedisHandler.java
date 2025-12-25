package org.zuma.runeHeim.redis;

import org.zuma.examplePlugin.core.redis.GlobalEventHandler;
import org.zuma.runeHeim.chat.ChatListener;
import org.zuma.runeHeim.chat.PrivateChatHandler;
import org.zuma.runeHeim.events.*;

public class RedisHandler {

    public static void registerEvents() {
        GlobalEventHandler.registerEvent(RuneChatSpyEvent.class, RuneChatSpyEvent.Payload.class);
        GlobalEventHandler.registerEvent(RuneGlobalChatBroadcastEvent.class, RuneGlobalChatBroadcastEvent.Payload.class);
        GlobalEventHandler.registerEvent(RuneMegaphoneChatEvent.class, RuneMegaphoneChatEvent.Payload.class);
        GlobalEventHandler.registerEvent(RunePrivateChatACKEvent.class, RunePrivateChatACKEvent.Payload.class);
        GlobalEventHandler.registerEvent(RunePrivateChatSYNEvent.class, RunePrivateChatSYNEvent.Payload.class);

        GlobalEventHandler.addListener(RuneChatSpyEvent.class, payload -> {
            ChatListener.onSpyEventReceived(payload.serverId, payload.sender, payload.content, payload.viewers);
        });

        GlobalEventHandler.addListener(RuneGlobalChatBroadcastEvent.class, payload -> {
            ChatListener.onGlobalChatEventReceived(payload.serverId, payload.sender, payload.content);
        });

        GlobalEventHandler.addListener(RuneMegaphoneChatEvent.class, payload -> {
            ChatListener.onMegaphoneEventReceived(payload.serverId, payload.sender, payload.content);
        });


        GlobalEventHandler.addListener(RunePrivateChatSYNEvent.class, payload -> {
            PrivateChatHandler.onSYNPrivateChatEventReceived(payload.serverId, payload.sender, payload.receiverName, payload.content, payload.messageUUID);
        });

        GlobalEventHandler.addListener(RunePrivateChatACKEvent.class, payload -> {
            PrivateChatHandler.onACKPrivateChatEventReceived(payload.serverId, payload.sender, payload.receiver, payload.content, payload.messageUUID);
        });
    }
}
