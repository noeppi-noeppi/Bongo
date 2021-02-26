package io.github.noeppi_noeppi.mods.bongo.util;

import com.google.common.collect.ImmutableList;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class Util {

    public static final List<DyeColor> PREFERRED_COLOR_ORDER = ImmutableList.of(
            DyeColor.ORANGE, DyeColor.LIME, DyeColor.LIGHT_BLUE, DyeColor.PINK, DyeColor.CYAN,
            DyeColor.YELLOW, DyeColor.RED, DyeColor.GREEN, DyeColor.BLUE, DyeColor.PURPLE,
            DyeColor.GRAY, DyeColor.MAGENTA, DyeColor.BLACK, DyeColor.WHITE, DyeColor.BROWN,
            DyeColor.LIGHT_GRAY
    );

    public static TextFormatting getTextFormatting(@Nullable DyeColor color) {
        if (color == null) {
            return TextFormatting.RESET;
        } else if (color == DyeColor.WHITE) {
            return TextFormatting.WHITE;
        } else if (color == DyeColor.ORANGE) {
            return TextFormatting.GOLD;
        } else if (color == DyeColor.MAGENTA) {
            return TextFormatting.RED;
        } else if (color == DyeColor.LIGHT_BLUE) {
            return TextFormatting.BLUE;
        } else if (color == DyeColor.YELLOW) {
            return TextFormatting.YELLOW;
        } else if (color == DyeColor.LIME) {
            return TextFormatting.GREEN;
        } else if (color == DyeColor.PINK) {
            return TextFormatting.LIGHT_PURPLE;
        } else if (color == DyeColor.GRAY) {
            return TextFormatting.DARK_GRAY;
        } else if (color == DyeColor.LIGHT_GRAY) {
            return TextFormatting.GRAY;
        } else if (color == DyeColor.CYAN) {
            return TextFormatting.AQUA;
        } else if (color == DyeColor.PURPLE) {
            return TextFormatting.DARK_PURPLE;
        } else if (color == DyeColor.BLUE) {
            return TextFormatting.DARK_BLUE;
        } else if (color == DyeColor.BROWN) {
            return TextFormatting.DARK_AQUA;
        } else if (color == DyeColor.GREEN) {
            return TextFormatting.DARK_GREEN;
        } else if (color == DyeColor.RED) {
            return TextFormatting.DARK_RED;
        } else if (color == DyeColor.BLACK) {
            return TextFormatting.BLACK;
        } else {
            return TextFormatting.RESET;
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
