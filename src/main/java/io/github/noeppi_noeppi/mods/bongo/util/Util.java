package io.github.noeppi_noeppi.mods.bongo.util;

import com.google.common.collect.ImmutableList;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.client.Minecraft;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraft.command.arguments.IArgumentSerializer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.List;
import java.util.UUID;

public class Util {

    public static final List<DyeColor> PREFERRED_COLOR__ORDER = ImmutableList.of(
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

    public static void broadcast(World world, ITextComponent message) {
        MinecraftServer server = world.getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> player.sendMessage(message, player.getUniqueID()));
        }
    }

    public static void broadcastExcept(World world, PlayerEntity exclude, ITextComponent message) {
        UUID uid = exclude.getGameProfile().getId();
        MinecraftServer server = world.getServer();
        if (server != null) {
            server.getPlayerList().getPlayers().forEach(player -> {
                if (!uid.equals(player.getGameProfile().getId()))
                    player.sendMessage(message, player.getUniqueID());
            });
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

    @Deprecated
    public static MinecraftServer getClientServer() {
        return DistExecutor.unsafeRunForDist(() -> Minecraft.getInstance()::getIntegratedServer, () -> () -> null);
    }

    public static void registerGenericCommandArgument(String name, Class<?> clazz, IArgumentSerializer<?> ias) {
        try {
            Method method = ObfuscationReflectionHelper.findMethod(ArgumentTypes.class, "func_218136_a", String.class, Class.class, IArgumentSerializer.class);
            method.setAccessible(true);
            method.invoke(null, name, clazz, ias);
        } catch (ReflectiveOperationException e) {
            throw new RuntimeException(e);
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
}
