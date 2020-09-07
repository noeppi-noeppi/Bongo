package io.github.noeppi_noeppi.mods.bongo.effect;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.world.server.ServerWorld;
import org.apache.logging.log4j.util.TriConsumer;

import java.util.ArrayList;
import java.util.List;

public class WinEffects {

    private static final List<TriConsumer<Bongo, ServerWorld, Team>> worldEffects = new ArrayList<>();

    public static void registerWorldEffect(TriConsumer<Bongo, ServerWorld, Team> effect) {
        worldEffects.add(effect);
    }

    public static void callWorldEffects(Bongo bongo, ServerWorld world, Team team) {
        worldEffects.forEach(c -> c.accept(bongo, world, team));
    }
}
