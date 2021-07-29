package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class BongoTaskEvent extends Event {
    
    private final Bongo bongo;
    private final ServerLevel level;
    private final ServerPlayer player;
    private final Task task;

    public BongoTaskEvent(Bongo bongo, ServerLevel level, ServerPlayer player, Task task) {
        this.bongo = bongo;
        this.level = level;
        this.player = player;
        this.task = task;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public Task getTask() {
        return task;
    }
}
