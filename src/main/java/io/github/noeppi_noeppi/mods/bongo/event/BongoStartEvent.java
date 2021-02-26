package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

public class BongoStartEvent extends Event {

    private final Bongo bongo;
    private final ServerWorld world;

    public BongoStartEvent(Bongo bongo, ServerWorld world) {
        this.bongo = bongo;
        this.world = world;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public static class World extends BongoStartEvent {

        public World(Bongo bongo, ServerWorld world) {
            super(bongo, world);
        }
    }

    public static class Player extends BongoStartEvent {

        private final ServerPlayerEntity player;

        public Player(Bongo bongo, ServerWorld world, ServerPlayerEntity player) {
            super(bongo, world);
            this.player = player;
        }

        public ServerPlayerEntity getPlayer() {
            return player;
        }
    }
}
