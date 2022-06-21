package io.github.noeppi_noeppi.mods.bongo.teleporters;

import com.mojang.serialization.Codec;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerTeleporters {
    
    public static final Codec<PlayerTeleporter> CODEC = Codec.STRING.xmap(PlayerTeleporters::getTeleporter, PlayerTeleporter::id);
    
    private static final Map<String, PlayerTeleporter> teleporters = new HashMap<>();

    public static PlayerTeleporter getTeleporter(String id) {
        if (id == null) {
            return PlayerTeleporterDefault.INSTANCE;
        } else {
            return teleporters.get(id.toLowerCase());
        }
    }

    public static void registerTeleporter(PlayerTeleporter type) {
        String id = type.id().toLowerCase();
        if (teleporters.containsKey(id)) {
            throw new IllegalStateException("Player Teleporter with id '" + id + "' is already registered.");
        } else {
            teleporters.put(id, type);
        }
    }

    public static Collection<PlayerTeleporter> getTeleporters() {
        return teleporters.values();
    }
}
