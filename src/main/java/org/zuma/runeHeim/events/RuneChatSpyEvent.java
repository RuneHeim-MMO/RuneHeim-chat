package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;
import org.zuma.runeHeim.chat.base.PlayerData;

public class RuneChatSpyEvent extends RedisEvent<RuneChatSpyEvent.Payload> {
    private final Payload payload;

    public RuneChatSpyEvent(Payload payload) {
        this.payload = payload;
    }

    public static class Payload extends RedisPayload {
        public PlayerData sender;
        public String content;
        public int viewers;

        public Payload(PlayerData sender, String content, int viewers) {
            this.sender = sender;
            this.content = content;
            this.viewers = viewers;
        }
    }

    @Override
    public Payload composePayload() {
        return payload;
    }
}
