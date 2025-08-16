package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;

import java.util.UUID;

public class RuneChatEvent extends RedisEvent<RuneChatEvent.Payload> {
    private final Payload payload;

    public RuneChatEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public String senderName;
        public UUID senderUUID;
        public String content;

        public Payload(String senderName, UUID senderUUID, String content) {
            this.senderName = senderName;
            this.senderUUID = senderUUID;
            this.content = content;
        }
    }

    @Override
    public Payload composePayload() {
        return payload;

    }
}
