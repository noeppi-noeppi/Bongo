package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public abstract class BongoStopEvent extends Event {
    
    private final Bongo bongo;
    private final ServerLevel level;

    private BongoStopEvent(Bongo bongo, ServerLevel level) {
        this.bongo = bongo;
        this.level = level;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public ServerLevel getLevel() {
        return level;
    }
    
    public static class Level extends BongoStopEvent {

        public Level(Bongo bongo, ServerLevel level) {
            super(bongo, level);
        }
    }
    
    public static class Player extends BongoStopEvent {

        private final ServerPlayer player;
        
        public Player(Bongo bongo, ServerLevel level, ServerPlayer player) {
            super(bongo, level);
            this.player = player;
        }

        public ServerPlayer getPlayer() {
            return player;
        }
    }
}
