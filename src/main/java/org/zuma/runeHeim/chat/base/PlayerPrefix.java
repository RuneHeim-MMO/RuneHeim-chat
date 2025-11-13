package org.zuma.runeHeim.chat.base;

import org.zuma.examplePlugin.core.utils.TextUtils;
import org.zuma.examplePlugin.domain.player.RunePlayer;

public record PlayerPrefix(String prefix, String supporterPrefix) {
    public static PlayerPrefix getPrefix(RunePlayer player) {
        return new PlayerPrefix(TextUtils.extractPlainText(player.getRole().getPrefix()), player.getRole().getSupporterRoleIcon());
    }
}
