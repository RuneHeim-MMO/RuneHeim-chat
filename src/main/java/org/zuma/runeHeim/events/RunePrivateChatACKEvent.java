package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;
import org.zuma.runeHeim.chat.base.PlayerData;

import java.util.List;
import java.util.UUID;

public class RunePrivateChatACKEvent extends RedisEvent<RunePrivateChatACKEvent.Payload> {
    private final Payload payload;

    public RunePrivateChatACKEvent(Payload payload) {
        this.payload = payload;
    }

    @Override
    public Payload composePayload() {
        return payload;
    }

    public static class Payload extends RedisPayload {
        public PlayerData sender, receiver;
        public String content;
        public UUID messageUUID;

        public Payload(PlayerData sender, PlayerData receiver, String content, UUID messsageUUID, int targetServerId) {
            this.sender = sender;
            this.receiver = receiver;
            this.content = content;
            this.messageUUID = messsageUUID;
            this.targetServers = List.of(targetServerId);
        }
    }
}
