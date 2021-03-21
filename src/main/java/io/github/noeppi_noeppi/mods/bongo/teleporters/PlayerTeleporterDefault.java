package io.github.noeppi_noeppi.mods.bongo.teleporters;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.compat.SkyblockIntegration;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Random;

public class PlayerTeleporterDefault implements PlayerTeleporter {
    
    public static final PlayerTeleporterDefault INSTANCE = new PlayerTeleporterDefault();
    
    private PlayerTeleporterDefault() {
        
    }
    
    @Override
    public String getId() {
        return "bongo.default";
    }

    @Override
    public void teleportTeam(Bongo bongo, ServerWorld gameWorld, Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random) {
        if (ModList.get().isLoaded("skyblockbuilder") && SkyblockIntegration.appliesFor(gameWorld)) {
            SkyblockIntegration.Teleporter.INSTANCE.teleportTeam(bongo, gameWorld, team, players, center, radius, random);
        } else {
            PlayerTeleporterStandard.INSTANCE.teleportTeam(bongo, gameWorld, team, players, center, radius, random);
        }
    }
}
