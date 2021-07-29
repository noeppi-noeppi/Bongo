package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporter;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

import java.util.List;

/**
 * Posted after a team got teleported. This is also posted when the no-teleporter is used.
 */
public class BongoTeleportedEvent extends Event {

    private final Bongo bongo;
    private final ServerLevel level;
    private final Team team;
    private final PlayerTeleporter teleporter;
    private final List<ServerPlayer> players;

    public BongoTeleportedEvent(Bongo bongo, ServerLevel level, Team team, PlayerTeleporter teleporter, List<ServerPlayer> players) {
        this.bongo = bongo;
        this.level = level;
        this.team = team;
        this.teleporter = teleporter;
        this.players = players;
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

    public PlayerTeleporter getTeleporter() {
        return teleporter;
    }

    public List<ServerPlayer> getPlayers() {
        return players;
    }
}
