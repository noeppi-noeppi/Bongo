package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;

/**
 * Posted on the forge bus to pick a world for the bingo game
 */
public class BongoPickWorldEvent extends Event {

    private final Bongo bongo;
    private final MinecraftServer server;
    private ServerWorld world;

    public BongoPickWorldEvent(Bongo bongo, ServerWorld world) {
        this.bongo = bongo;
        this.server = world.getServer();
        this.world = world;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ServerWorld getWorld() {
        return world;
    }

    public void setWorld(ServerWorld world) {
        this.world = world;
    }
}
