package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.network.chat.Component;
import net.minecraftforge.eventbus.api.Event;

import java.util.Set;

/**
 * This event is posted on the forge bus whenever something attempts to change the teams of
 * many players at once. If the event is canceled, the players won't change team;
 * Use setFailureMessage to adjust the failure message if the event is canceled.
 */
public class BongoChangeManyTeamsEvent extends Event { 
    
    private final Bongo bongo;
    private final Set<Team> teamsToAssign;
    private Component failureMessage;

    public BongoChangeManyTeamsEvent(Bongo bongo, Set<Team> teamsToAssign, Component failureMessage) {
        this.bongo = bongo;
        this.teamsToAssign = teamsToAssign;
        this.failureMessage = failureMessage;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public Set<Team> getTeamsToAssign() {
        return teamsToAssign;
    }

    public Component getFailureMessage() {
        return failureMessage;
    }

    public void setFailureMessage(Component failureMessage) {
        this.failureMessage = failureMessage;
    }

    @Override
    public boolean isCancelable() {
        return true;
    }
}
