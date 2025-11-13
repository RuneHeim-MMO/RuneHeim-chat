package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;
import org.zuma.runeHeim.chat.base.PlayerData;

import java.util.UUID;

public class RunePrivateChatSYNEvent extends RedisEvent<RunePrivateChatSYNEvent.Payload> {
    private final Payload payload;

    public RunePrivateChatSYNEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public PlayerData sender, receiver;
        public String content, receiverName;
        public UUID messageUUID;

        public Payload(PlayerData sender, String receiverName, String content, UUID messsageUUID) {
            this.sender = sender;
            this.receiverName = receiverName;
            this.content = content;
            this.messageUUID = messsageUUID;
        }
    }

    @Override
    public Payload composePayload() {
        this.payload.replayAtOrigin = false;
        return payload;
    }
}
