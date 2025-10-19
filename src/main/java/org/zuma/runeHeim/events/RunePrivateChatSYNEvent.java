package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.permissions.PlayerRole;
import org.zuma.examplePlugin.core.redis.RedisEvent;

import java.util.UUID;

public class RunePrivateChatSYNEvent extends RedisEvent<RunePrivateChatSYNEvent.Payload> {
    private final Payload payload;

    public RunePrivateChatSYNEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public String senderName, receiverName, content;
        public UUID senderUUID, messageUUID;
        public PlayerRole senderRank;

        public Payload(String senderName, PlayerRole senderRank, UUID senderUUID, String receiverName, String content, UUID messsageUUID) {
            this.senderName = senderName;
            this.senderRank = senderRank;
            this.senderUUID = senderUUID;
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
