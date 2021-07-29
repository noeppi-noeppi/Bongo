package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.eventbus.api.Event;

public class BongoStartEvent extends Event {

    private final Bongo bongo;
    private final ServerLevel level;

    private BongoStartEvent(Bongo bongo, ServerLevel level) {
        this.bongo = bongo;
        this.level = level;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public static class Level extends BongoStartEvent {

        public Level(Bongo bongo, ServerLevel level) {
            super(bongo, level);
        }
    }

    public static class Player extends BongoStartEvent {

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
