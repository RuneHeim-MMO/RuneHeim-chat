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
        public String senderName, senderPrefix, senderSupporterPrefix, receiverName, receiverPrefix, receiverSupporterPrefix, content;
        public UUID senderUUID, messageUUID;

        public Payload(String senderName, String senderPrefix, String senderSupporterPrefix, UUID senderUUID, String receiverName, String receiverPrefix, String receiverSupporterPrefix, String content, UUID messsageUUID, int targetServerId) {
            this.senderName = senderName;
            this.senderUUID = senderUUID;
            this.senderPrefix = senderPrefix;
            this.receiverName = receiverName;
            this.receiverPrefix = receiverPrefix;
            this.content = content;
            this.messageUUID = messsageUUID;
            this.receiverSupporterPrefix = receiverSupporterPrefix;
            this.senderSupporterPrefix = senderSupporterPrefix;
            this.targetServers = List.of(targetServerId);
        }
    }

    @Override
    public Payload composePayload() {
        return payload;
    }
}
