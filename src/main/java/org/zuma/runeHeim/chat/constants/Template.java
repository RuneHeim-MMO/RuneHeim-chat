package org.zuma.runeHeim.chat.constants;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.jetbrains.annotations.NotNull;
import org.zuma.examplePlugin.core.constants.Icon;

public enum Template {
    PRIVATE_MESSAGE(Component.text("[PM] ", TextColor.color(77, 128, 240))),
    GLOBAL_MESSAGE(Component.text("[Global] ", TextColor.color(255, 85, 0))),
    SERVER_MESSAGE(Component.text("[Server] ", NamedTextColor.RED)),
    PARTY_MESSAGE(Icon.PARTY.asComponent().append(Component.text("[Party] ", NamedTextColor.YELLOW))),
    SEPARATOR(Component.text(" » ", TextColor.color(150, 150, 150)));

    private final Component text;

    Template(@NotNull Component text) {
        this.text = text;
    }

    public Component component() {
        return text;
    }
}
