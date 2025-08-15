package org.zuma.runeHeim.chat;

import io.papermc.paper.chat.ChatRenderer;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.util.TextUtils;

import java.util.Map;

public class Renderer {
    public static Component renderPlayerMessage(RunePlayer senderRunePlayer, Player source, Component sourceDisplayName, Component message, Audience viewer) {
        int senderCmbLvl = senderRunePlayer.getCombatLvl();

        String namePlain = PlainTextComponentSerializer.plainText().serialize(sourceDisplayName);
        Component displayName = Component.text(namePlain);
        Component levelComponent = TextUtils.getLevelGradientComponent(senderCmbLvl);


        return Component.text()
                .append(Component.text("[Lv.", levelComponent.color()).append(levelComponent).append(Component.text("] ")))
                .append(displayName.color(NamedTextColor.WHITE))
                .append(Component.text(" » ", TextColor.color(150, 150, 150)))
                .append(message)
                .build();
    }


}
