package org.zuma.runeHeim.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;

public class TextUtils {
    public static Component getLevelGradientComponent(int level) {
        // Gradient stops
        int[] stops = {10, 25, 50, 75, 100};
        TextColor[] colors = {
                TextColor.color(0x55FF55), // green
                TextColor.color(0x00AA00), // dark green
                TextColor.color(0xFFFF55), // yellow
                TextColor.color(0xFFAA00), // gold
                TextColor.color(0xFF5555)  // red
        };

        // Below first stop
        if (level <= stops[0]) {
            return Component.text(String.valueOf(level), colors[0]);
        }

        // Above last stop
        if (level >= stops[stops.length - 1]) {
            return Component.text(String.valueOf(level), colors[colors.length - 1]);
        }

        // Find which two stops the level is between
        int startIndex = 0;
        while (startIndex < stops.length && level > stops[startIndex]) startIndex++;
        int endIndex = startIndex;
        startIndex--;

        int startLevel = stops[startIndex];
        int endLevel = stops[endIndex];
        TextColor startColor = colors[startIndex];
        TextColor endColor = colors[endIndex];

        // Interpolate
        float ratio = (float) (level - startLevel) / (endLevel - startLevel);
        int r = (int) (startColor.red() + (endColor.red() - startColor.red()) * ratio);
        int g = (int) (startColor.green() + (endColor.green() - startColor.green()) * ratio);
        int b = (int) (startColor.blue() + (endColor.blue() - startColor.blue()) * ratio);

        return Component.text(String.valueOf(level), TextColor.color(r, g, b));
    }

}
