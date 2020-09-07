package io.github.noeppi_noeppi.mods.bongo.effect;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.entity.player.ServerPlayerEntity;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;

public class TaskEffects {

    private static final List<TriConsumer<Bongo, ServerPlayerEntity, Task>> playerEffects = new ArrayList<>();

    public static void registerPlayerEffect(TriConsumer<Bongo, ServerPlayerEntity, Task> effect) {
        playerEffects.add(effect);
    }

    public static void callPlayerEffects(Bongo bongo, ServerPlayerEntity player, Task task) {
        playerEffects.forEach(c -> c.accept(bongo, player, task));
    }
}
