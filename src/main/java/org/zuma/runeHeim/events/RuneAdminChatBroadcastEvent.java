package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;
import org.zuma.runeHeim.chat.base.PlayerData;

public class RuneAdminChatBroadcastEvent extends RedisEvent<RuneAdminChatBroadcastEvent.Payload> {
    private final Payload payload;

    public RuneAdminChatBroadcastEvent(Payload payload) {
        this.payload = payload;
    }

    @Override
    public Payload composePayload() {
        return payload;
    }

    public static class Payload extends RedisPayload {
        public PlayerData sender;
        public String content;

        public Payload(PlayerData sender, String content) {
            this.sender = sender;
            this.content = content;
        }
    }
}
