package org.zuma.runeHeim.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.constants.Color;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.chat.base.PlayerData;
import org.zuma.runeHeim.chat.proximity.ProximityManager;
import org.zuma.runeHeim.events.RuneAdminChatBroadcastEvent;
import org.zuma.runeHeim.events.RuneGlobalChatBroadcastEvent;

import java.util.Collection;
import java.util.List;

public class StaffChatCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        ProximityManager manager = RuneHeimChat.getProximityManager();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can be only used by players.").color(Color.ERROR.asColor()));
            return;
        }

        if (!player.hasPermission("runeheim.admin.chat")) {
            player.sendMessage(Component.text("You do not have access to that command.", Color.ERROR.asColor()));
            return;
        }

        RunePlayer runePlayer = RuneQuestPlugin.getPlayerManager().getRunePlayer(player);

        if (runePlayer == null) {
            player.sendMessage(Component.text("Player instance not found.", Color.ERROR.asColor()));
            return;
        }

        if (args.length >= 1) {
            String message = String.join(" ", List.of(args));
            RuneAdminChatBroadcastEvent chatEvent = new RuneAdminChatBroadcastEvent(new RuneAdminChatBroadcastEvent.Payload(PlayerData.fromPlayerData(runePlayer), message));
            Bukkit.getServer().getPluginManager().callEvent(chatEvent);
            return;

        }

        player.sendMessage(Component.text("Usage: /s <msg> - send staff chat message across server/network.", Color.ERROR.asColor()));

    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return List.of();
    }

}
