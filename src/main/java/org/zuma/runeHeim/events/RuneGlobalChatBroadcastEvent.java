package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.permissions.PlayerRole;
import org.zuma.examplePlugin.core.redis.RedisEvent;

import java.util.UUID;

public class RuneGlobalChatBroadcastEvent extends RedisEvent<RuneGlobalChatBroadcastEvent.Payload> {
    private final Payload payload;

    public RuneGlobalChatBroadcastEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public String content, senderName, senderPrefix, senderSupporterPrefix;
        public UUID senderUUID;

        public Payload(String senderName, String senderPrefix, String senderSupporterPrefix, UUID senderUUID, String content) {
            this.senderName = senderName;
            this.senderPrefix = senderPrefix;
            this.senderSupporterPrefix = senderSupporterPrefix;
            this.senderUUID = senderUUID;
            this.content = content;
        }
    }

    @Override
    public Payload composePayload() {
        return payload;
    }
}
