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
import org.zuma.runeHeim.chat.base.PlayerData;
import org.zuma.runeHeim.events.StaffHelpChatBroadcastEvent;

import java.util.Collection;
import java.util.List;

public class StaffHelpCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can be only used by players.").color(Color.ERROR.asColor()));
            return;
        }

        if (!player.hasPermission("runeheim.chat.staffhelp")) {
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
            StaffHelpChatBroadcastEvent chatEvent = new StaffHelpChatBroadcastEvent(new StaffHelpChatBroadcastEvent.Payload(PlayerData.fromPlayerData(runePlayer), message));
            Bukkit.getServer().getPluginManager().callEvent(chatEvent);
            player.sendMessage(Component.text("Your message has been sent to online staff members.", Color.SUCCESS.asColor()));
            return;

        }

        player.sendMessage(Component.text("Usage: /staffhelp <msg> - send message to active staff members.", Color.ERROR.asColor()));

    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return List.of();
    }

}
