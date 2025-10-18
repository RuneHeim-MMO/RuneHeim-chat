package org.zuma.runeHeim.commands;

import io.papermc.paper.command.brigadier.BasicCommand;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import net.kyori.adventure.text.Component;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.core.constants.Color;
import org.zuma.runeHeim.RuneHeimChat;
import org.zuma.runeHeim.chat.PrivateChatHandler;
import org.zuma.runeHeim.chat.proximity.ProximityManager;

import java.util.Collection;
import java.util.List;

public class ReplyCommand implements BasicCommand {
    @Override
    public void execute(CommandSourceStack commandSourceStack, String[] args) {
        CommandSender sender = commandSourceStack.getSender();
        ProximityManager manager = RuneHeimChat.getProximityManager();

        if (!(sender instanceof Player player)) {
            sender.sendMessage(Component.text("This command can be only used by players.").color(Color.ERROR.asColor()));
            return;
        }


        if (args.length >= 1 && args[0].length() <= 32) {
            String targetName = PrivateChatHandler.getRecentMessageSender(player.getUniqueId());

            if (targetName == null) {
                player.sendMessage(Component.text("You have no one to reply to.", Color.ERROR.asColor()));
                return;
            }

            String message = String.join(" ", List.of(args));
            PrivateChatHandler.sendPrivateMessage(player, targetName, message);
            return;

        }

        player.sendMessage(Component.text("Usage: /r <msg> - reply to last private message.", Color.ERROR.asColor()));

    }

    @Override
    public Collection<String> suggest(CommandSourceStack source, String[] args) {
        return List.of();
    }

}
