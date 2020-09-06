package io.github.noeppi_noeppi.mods.bongo.effect;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;

public class StartingEffects {

    private static final List<BiConsumer<Bongo, ServerWorld>> worldEffects = new ArrayList<>();
    private static final List<BiConsumer<Bongo, ServerPlayerEntity>> playerEffects = new ArrayList<>();

    public static void registerWorldEffect(BiConsumer<Bongo, ServerWorld> effect) {
        worldEffects.add(effect);
    }

    public static void registerPlayerEffect(BiConsumer<Bongo, ServerPlayerEntity> effect) {
        playerEffects.add(effect);
    }

    public static void callWorldEffects(Bongo bongo, ServerWorld world) {
        worldEffects.forEach(c -> c.accept(bongo, world));
    }

    public static void callPlayerEffects(Bongo bongo, ServerPlayerEntity player) {
        playerEffects.forEach(c -> c.accept(bongo, player));
    }
}
