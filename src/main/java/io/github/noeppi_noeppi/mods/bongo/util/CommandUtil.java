package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.brigadier.context.CommandContext;

public class CommandUtil {

    public static <T> T getArgumentOrDefault(CommandContext<?> ctx, String name, Class<T> clazz, T defaultValue) {
        try {
            return ctx.getArgument(name, clazz);
        } catch (IllegalArgumentException e) {
            return defaultValue;
        }
    }
}
