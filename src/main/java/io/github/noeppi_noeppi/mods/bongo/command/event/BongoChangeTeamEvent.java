package io.github.noeppi_noeppi.mods.bongo.command.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TextComponent;
import net.minecraftforge.eventbus.api.Event;

import javax.annotation.Nullable;

/**
 * This event is posted on the forge bus whenever a player tries to change the team via a command and
 * all native checks by bongo succeeded. If the event is canceled, the player won't change team;
 * Use setFailureMessage to adjust the failure message if the event is canceled.
 */
public class BongoChangeTeamEvent extends Event {

    private final ServerPlayerEntity player;
    private final Bongo bongo;
    @Nullable
    private final Team oldTeam;
    @Nullable
    private final Team newTeam;
    private TextComponent failureMessage; 

    public BongoChangeTeamEvent(ServerPlayerEntity player, Bongo bongo, @Nullable Team oldTeam, @Nullable Team newTeam, TextComponent failureMessage) {
        this.player = player;
        this.bongo = bongo;
        this.oldTeam = oldTeam;
        this.newTeam = newTeam;
        this.failureMessage = failureMessage;
    }

    public ServerPlayerEntity getPlayer() {
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

    public TextComponent getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(TextComponent failureMessage) {
        this.failureMessage = failureMessage;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
