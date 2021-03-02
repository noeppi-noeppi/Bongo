package io.github.noeppi_noeppi.mods.bongo.util;

import com.google.common.collect.ImmutableList;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Util {

    public static final Comparator<ResourceLocation> COMPARE_RESOURCE = Comparator.comparing(ResourceLocation::getNamespace).thenComparing(ResourceLocation::getPath);
    
    public static final List<DyeColor> PREFERRED_COLOR_ORDER = ImmutableList.of(
            DyeColor.ORANGE, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.PINK, DyeColor.CYAN,
            DyeColor.YELLOW, DyeColor.RED, DyeColor.GREEN, DyeColor.BLUE, DyeColor.PURPLE,
            DyeColor.GRAY, DyeColor.MAGENTA, DyeColor.BLACK, DyeColor.WHITE, DyeColor.BROWN,
            DyeColor.LIGHT_GRAY
    );
    
    private static final Map<DyeColor, Color> COLOR_CACHE = new HashMap<>();

    public static Style getTextFormatting(@Nullable DyeColor color) {
        if (color == null) {
            return Style.EMPTY.applyFormatting(TextFormatting.RESET);
        } else {
            if (!COLOR_CACHE.containsKey(color)) {
                int colorValue = color.getColorValue();
                float[] hsb = java.awt.Color.RGBtoHSB((colorValue >> 16) & 0xFF, (colorValue >> 8) & 0xFF, colorValue & 0xFF, null);
                COLOR_CACHE.put(color, Color.fromInt(java.awt.Color.HSBtoRGB(hsb[0], Math.min(1, hsb[1] + 0.1f), Math.min(1, hsb[2] + 0.1f))));
            }
            return Style.EMPTY.setColor(COLOR_CACHE.get(color));
        }
    }

    public static void broadcastTeam(World world, Team team, ITextComponent message) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> {
                if (team.hasPlayer(player))
                    player.sendMessage(message, player.getUniqueID());
            });
        }
    }

    public static boolean matchesNBT(@Nullable CompoundNBT required, @Nullable CompoundNBT actual) {
        if (required == null || required.isEmpty())
            return true;

        if (actual == null || actual.isEmpty())
            return false;

        CompoundNBT copy = actual.copy();
        copy.merge(required);
        return copy.equals(actual);
    }

    public static String formatTime(int hours, int minutes, int seconds) {
        return hours > 0 ? (hours + ":") : "" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds);
    }

    public static String formatTime(int hours, int minutes, int seconds, int decimal) {
        return hours > 0 ? (hours + ":") : "" + String.format("%02d", minutes) + ":" + String.format("%02d", seconds) + "." + decimal;
    }
}
