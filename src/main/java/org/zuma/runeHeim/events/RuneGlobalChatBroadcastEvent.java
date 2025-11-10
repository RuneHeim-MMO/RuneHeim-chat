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
        public String senderName;
        public String senderPrefix;
        public UUID senderUUID;
        public String content;

        public Payload(String senderName, String senderPrefix, UUID senderUUID, String content) {
            this.senderName = senderName;
            this.senderPrefix = senderPrefix;
            this.senderUUID = senderUUID;
            this.content = content;
        }
    }

    @Override
    public Payload composePayload() {
        return payload;
    }
}
