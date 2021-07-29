package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.network.chat.BaseComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * This event is posted on the forge bus whenever a player tries to change the team via a command and
 * all native checks by bongo succeeded. If the event is canceled, the player won't change team;
 * Use setFailureMessage to adjust the failure message if the event is canceled.
 */
public class BongoChangeTeamEvent extends Event {

    private final ServerPlayer player;
    private final Bongo bongo;
    @Nullable
    private final Team oldTeam;
    @Nullable
    private final Team newTeam;
    private BaseComponent failureMessage; 

    public BongoChangeTeamEvent(ServerPlayer player, Bongo bongo, @Nullable Team oldTeam, @Nullable Team newTeam, BaseComponent failureMessage) {
        this.player = player;
        this.bongo = bongo;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
        this.failureMessage = failureMessage;
    }

    public ServerPlayer getPlayer() {
        return player;
    }

    public Bongo getBongo() {
        return bongo;
    }

    @Nullable
    public Team getOldTeam() {
        return oldTeam;
    }

    @Nullable
    public Team getNewTeam() {
        return newTeam;
    }

    public BaseComponent getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(BaseComponent failureMessage) {
        this.failureMessage = failureMessage;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
