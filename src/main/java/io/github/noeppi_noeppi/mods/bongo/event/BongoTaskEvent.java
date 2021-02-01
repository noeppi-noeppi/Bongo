package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class BongoTaskEvent extends Event {
    
    private final Bongo bongo;
    private final ServerWorld world;
    private final ServerPlayerEntity player;
    private final Task task;

    public BongoTaskEvent(Bongo bongo, ServerWorld world, ServerPlayerEntity player, Task task) {
        this.bongo = bongo;
        this.world = world;
        this.player = player;
        this.task = task;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public ServerPlayerEntity getPlayer() {
        return player;
    }

    public Task getTask() {
        return task;
    }
}
