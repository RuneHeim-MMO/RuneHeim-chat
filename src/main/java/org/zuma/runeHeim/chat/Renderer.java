package org.zuma.runeHeim.chat;

import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.constants.Icon;
import org.zuma.examplePlugin.domain.party.Party;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.chat.constants.Template;
import org.zuma.runeHeim.chat.proximity.BroadcastMode;
import org.zuma.runeHeim.util.ChatTextUtils;

public class Renderer {
    public static Component renderPlayerMessage(RunePlayer senderRunePlayer, Player source, Component sourceDisplayName, Component message, Audience viewer, BroadcastMode mode) {
        int senderCmbLvl = senderRunePlayer.getCombatLvl();
        Party senderParty = RuneQuestPlugin.getPartyManager().getPlayersParty(senderRunePlayer);
        boolean isPartyChat = mode == BroadcastMode.PARTY;

        String namePlain = PlainTextComponentSerializer.plainText().serialize(sourceDisplayName);
        Component displayName = Component.text(namePlain);
        Component levelComponent = ChatTextUtils.getLevelGradientComponent(senderCmbLvl);
        Component partyComponent = Component.empty();
        Component playerPrefix = !isPartyChat ? senderRunePlayer.getRole().getPrefix() : Component.empty();
        Component supporterPrefix = !isPartyChat ? Component.text(senderRunePlayer.getRole().getSupporterRoleIcon()) : Component.empty();
        NamedTextColor usernameColor = isPartyChat ? NamedTextColor.YELLOW : NamedTextColor.WHITE;

        if (viewer instanceof Player playerViewer) {
            RunePlayer viewerRunePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(playerViewer);

            if (viewerRunePlayer != null && senderParty != null) {
                if (mode != BroadcastMode.PARTY && senderParty.getMemberUUIDs().contains(viewerRunePlayer.getUUID()) && !senderRunePlayer.getUUID().equals(viewerRunePlayer.getUUID())) {
                    partyComponent = Icon.PARTY.asComponent(); // Show party icon only to party members in party chat
                }
            }
        }

        Component prefix = Component.empty();

        switch (mode) {
            case SERVER -> prefix = Template.SERVER_MESSAGE.component();
            case PARTY -> prefix = Template.PARTY_MESSAGE.component();
        }

        return Component.text()
                .append(prefix)
                .append(supporterPrefix)
                .append(Component.text("[Lv.", levelComponent.color()).append(levelComponent).append(Component.text("] ")))
                .append(Component.text("• ")
                        .append(partyComponent)
                        .append(playerPrefix)
                        .append(displayName.color(usernameColor)))
                .append(Template.SEPARATOR.component())
                .append(message)
                .build();
    }


}
