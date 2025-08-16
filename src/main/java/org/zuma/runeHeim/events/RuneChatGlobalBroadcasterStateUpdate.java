package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;

public class RuneChatGlobalBroadcasterStateUpdate extends RedisEvent<RuneChatGlobalBroadcasterStateUpdate.Payload> {
    private final boolean sendGlobalEvents;

    public RuneChatGlobalBroadcasterStateUpdate(boolean sendGlobalEvents) {
        this.sendGlobalEvents = sendGlobalEvents;
    }

    public static class Payload extends RedisPayload {
        public boolean sendGlobalEvents;
    }

    @Override
    public Payload composePayload() {
        Payload newPayload = new Payload();

        newPayload.sendGlobalEvents = this.sendGlobalEvents;

        return newPayload;

    }
}
