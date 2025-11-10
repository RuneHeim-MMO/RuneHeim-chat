package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.permissions.PlayerRole;
import org.zuma.examplePlugin.core.redis.RedisEvent;

import java.util.UUID;

public class RuneChatSpyEvent extends RedisEvent<RuneChatSpyEvent.Payload> {
    private final Payload payload;

    public RuneChatSpyEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public String senderName, content, senderPrefix;
        public UUID senderUUID;
        public int viewers;

        public Payload(String senderName, String senderPrefix, UUID senderUUID, String content, int viewers) {
            this.senderName = senderName;
            this.senderPrefix = senderPrefix;
            this.senderUUID = senderUUID;
            this.content = content;
            this.viewers = viewers;
        }
    }

    @Override
    public Payload composePayload() {
        return payload;
    }
}
