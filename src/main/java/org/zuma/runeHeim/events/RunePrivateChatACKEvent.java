package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.permissions.PlayerRole;
import org.zuma.examplePlugin.core.redis.RedisEvent;

import java.util.List;
import java.util.UUID;

public class RunePrivateChatACKEvent extends RedisEvent<RunePrivateChatACKEvent.Payload> {
    private final Payload payload;

    public RunePrivateChatACKEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public String senderName, receiverName, content;
        public UUID senderUUID, messageUUID;
        public PlayerRole senderRank, receiverRank;

        public Payload(String senderName,PlayerRole senderRank, UUID senderUUID, String receiverName, PlayerRole receiverRank, String content, UUID messsageUUID, int targetServerId) {
            this.senderName = senderName;
            this.senderUUID = senderUUID;
            this.senderRank = senderRank;
            this.receiverName = receiverName;
            this.receiverRank = receiverRank;
            this.content = content;
            this.messageUUID = messsageUUID;
            this.targetServers = List.of(targetServerId);
        }
    }

    @Override
    public Payload composePayload() {
        return payload;
    }
}
