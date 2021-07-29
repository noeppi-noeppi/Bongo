package io.github.noeppi_noeppi.mods.bongo.event;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraftforge.eventbus.api.Event;

/**
 * Posted on the forge bus to pick a world for the bingo game
 */
public class BongoPickLevelEvent extends Event {

    private final Bongo bongo;
    private final MinecraftServer server;
    private ServerLevel level;

    public BongoPickLevelEvent(Bongo bongo, ServerLevel level) {
        this.bongo = bongo;
        this.server = level.getServer();
        this.level = level;
    }

    public Bongo getBongo() {
        return bongo;
    }

    public MinecraftServer getServer() {
        return server;
    }

    public ServerLevel getLevel() {
        return level;
    }

    public void setLevel(ServerLevel level) {
        this.level = level;
    }
}
