package io.github.noeppi_noeppi.mods.bongo.teleporters;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class PlayerTeleporters {
    
    private static final Map<String, PlayerTeleporter> teleporters = new HashMap<>();

    public static PlayerTeleporter getTeleporter(String id) {
        if (id == null)
            return null;
        return teleporters.get(id.toLowerCase());
    }

    public static void registerTeleporter(PlayerTeleporter type) {
        String id = type.getId().toLowerCase();
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
