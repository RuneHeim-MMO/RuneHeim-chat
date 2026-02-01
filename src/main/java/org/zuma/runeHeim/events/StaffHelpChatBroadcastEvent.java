package org.zuma.runeHeim.events;

import org.zuma.examplePlugin.core.redis.RedisEvent;
import org.zuma.runeHeim.chat.base.PlayerData;

public class StaffHelpChatBroadcastEvent extends RedisEvent<StaffHelpChatBroadcastEvent.Payload> {
    private final Payload payload;

    public StaffHelpChatBroadcastEvent(Payload payload) {
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
