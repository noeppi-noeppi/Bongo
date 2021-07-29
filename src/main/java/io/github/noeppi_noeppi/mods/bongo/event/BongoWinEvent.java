package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

public class BongoWinEvent extends Event {
    
    private final Bongo bongo;
    private final ServerLevel level;
    private final Team team;

    public BongoWinEvent(Bongo bongo, ServerLevel level, Team team) {
        this.bongo = bongo;
        this.level = level;
        this.team = team;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public Team getTeam() {
        return team;
    }
}
