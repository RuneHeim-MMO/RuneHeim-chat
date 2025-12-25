package org.zuma.runeHeim.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.title.Title;
import org.bukkit.entity.Player;
import org.zuma.examplePlugin.core.constants.Color;
import org.zuma.examplePlugin.core.constants.SoundType;
import org.zuma.examplePlugin.domain.player.RunePlayer;

import java.time.Duration;

public class HudUtils {
    public static void showProfanityWarning(RunePlayer player) {
        Player bukkitPlayer = player.asBukkitPlayer();

        if (bukkitPlayer == null || !bukkitPlayer.isOnline()) {
            return;
        }

        player.asBukkitPlayer().showTitle(Title.title(
                Component.empty(),
                Component.text("Profanity is not tolerated here. This event has been logged.").color(Color.WARNING.asColor()),
                Title.Times.times(Duration.ZERO, Duration.ofSeconds(5L), Duration.ofMillis(1000L))
        ));
        SoundType.FAIL_ACTION.play(player.asBukkitPlayer(), false);

       /* Bukkit.getScheduler().runTaskLater(RuneHeimChat.getInstance(), () -> {
            if (!bukkitPlayer.isOnline()) return;

            bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 20 * 5, 255, false, false, false));
            bukkitPlayer.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 20 * 5, 200, false, false, false));

            player.asBukkitPlayer().showTitle(Title.title(
                    Component.text(StrikeHandler.getPlayerStrikes(bukkitPlayer.getUniqueId()) % StrikeHandler.getThreshold() + " / " + StrikeHandler.getThreshold()).color(Color.ERROR.asColor()),
                    Component.text("Repeated offenses will result in a permanent ban.").color(Color.ERROR.asColor()),
                    Title.Times.times(Duration.ZERO, Duration.ofSeconds(5L), Duration.ofMillis(1500L))
            ));

            bukkitPlayer.playSound(bukkitPlayer.getLocation(), Sound.ENTITY_WITHER_SPAWN, 1.0f, 0.5f);

        }, 130L); */
    }

}
