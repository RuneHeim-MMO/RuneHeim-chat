package org.zuma.runeHeim.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.chat.constants.Template;
import org.zuma.runeHeim.chat.proximity.BroadcastMode;
import org.zuma.runeHeim.util.TextUtils;

public class Renderer {
    public static Component renderPlayerMessage(RunePlayer senderRunePlayer, Player source, Component sourceDisplayName, Component message, Audience viewer, BroadcastMode mode) {
        int senderCmbLvl = senderRunePlayer.getCombatLvl();

        String namePlain = PlainTextComponentSerializer.plainText().serialize(sourceDisplayName);
        Component displayName = Component.text(namePlain);
        Component levelComponent = TextUtils.getLevelGradientComponent(senderCmbLvl);


        return Component.text()
                .append(mode == BroadcastMode.SERVER ? Template.SERVER_MESSAGE.component() : Component.empty())
                .append(Component.text("[Lv.", levelComponent.color()).append(levelComponent).append(Component.text("] ")))
                .append(Component.text("• ").append(senderRunePlayer.getRole().getPrefix()).append(displayName.color(NamedTextColor.WHITE)))
                .append(Template.SEPARATOR.component())
                .append(message)
                .build();
    }


}
