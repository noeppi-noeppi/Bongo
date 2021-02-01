package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class BongoWinEvent extends Event {
    
    private final Bongo bongo;
    private final ServerWorld world;
    private final Team team;

    public BongoWinEvent(Bongo bongo, ServerWorld world, Team team) {
        this.bongo = bongo;
        this.world = world;
        this.team = team;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public Team getTeam() {
        return team;
    }
}
