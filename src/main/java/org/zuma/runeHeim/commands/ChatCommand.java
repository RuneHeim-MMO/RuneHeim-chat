package org.zuma.runeHeim.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.RuneQuestPlugin;
import org.zuma.examplePlugin.core.constants.Color;
import org.zuma.examplePlugin.domain.player.RunePlayer;
import org.zuma.runeHeim.ProximityMode;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.chat.ProximityManager;

import java.util.Collection;
import java.util.List;

public class ChatCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        ProximityManager manager = RuneHeimChat.getProximityManager();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can be only used by players.").color(Color.ERROR.asColor()));
            return;
        }

        if (!player.hasPermission("runeheim.chat")) {
            player.sendMessage(Component.text("You do not have access to that command.", Color.ERROR.asColor()));
            return;
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("proximity")) {
            if (!player.hasPermission("runeheim.chat.proximity")) {
                player.sendMessage(Component.text("You do not have access to that command.", Color.ERROR.asColor()));
                return;
            }

            switch (args[1].toLowerCase()) {
                case "local": {
                    if (manager.getMode(player.getUniqueId()) == ProximityMode.LOCAL) {
                        player.sendMessage(Component.text("You are already in local chat mode.", Color.ERROR.asColor()));
                        return;
                    }


                    manager.setMode(player.getUniqueId(), ProximityMode.LOCAL);
                    player.sendMessage(Component.text("You have switched to local chat mode.", Color.SUCCESS.asColor()));
                    return;

                }
                case "server": {
                    if (!player.hasPermission("runeheim.chat.proximity.server")) {
                        player.sendMessage(Component.text("You don't have permission to use server chat mode.", Color.ERROR.asColor()));
                        return;
                    }

                    if (manager.getMode(player.getUniqueId()) == ProximityMode.SERVER) {
                        player.sendMessage(Component.text("You are already in server chat mode.", Color.ERROR.asColor()));
                        return;
                    }

                    manager.setMode(player.getUniqueId(), ProximityMode.SERVER);
                    player.sendMessage(Component.text("You have switched to server chat mode.", Color.SUCCESS.asColor()));
                    return;
                }

                case "network": {
                    if (!player.hasPermission("runeheim.chat.proximity.network")) {
                        player.sendMessage(Component.text("You don't have permission to use network chat mode.", Color.ERROR.asColor()));
                        return;
                    }

                    if (manager.getMode(player.getUniqueId()) == ProximityMode.NETWORK) {
                        player.sendMessage(Component.text("You are already in network chat mode.", Color.ERROR.asColor()));
                        return;
                    }

                    manager.setMode(player.getUniqueId(), ProximityMode.NETWORK);
                    player.sendMessage(Component.text("You have switched to network chat mode.", Color.SUCCESS.asColor()));
                    return;
                }
            }

        }

        player.sendMessage(Component.text("Usage: /chat proximity local|server|network", Color.ERROR.asColor()));

    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        if (args.length == 1) {
            return List.of("proximity");
        }

        if (args.length == 2 && args[0].equalsIgnoreCase("proximity")) {
            return List.of("local", "server", "network");
        }

        // No suggestions otherwise
        return List.of();
    }

}
