package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;

import java.util.UUID;

public class RunePrivateChatACKEvent extends RedisEvent<RunePrivateChatACKEvent.Payload> {
    private final Payload payload;

    public RunePrivateChatACKEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public String senderName, receiverName,content;
        public UUID senderUUID, messageUUID;

        public Payload(String senderName, UUID senderUUID, String receiverName, String content, UUID messsageUUID) {
            this.senderName = senderName;
            this.senderUUID = senderUUID;
            this.receiverName = receiverName;
            this.content = content;
            this.messageUUID = messsageUUID;
        }
    }

    @Override
    public Payload composePayload() {
        return payload;
    }
}
