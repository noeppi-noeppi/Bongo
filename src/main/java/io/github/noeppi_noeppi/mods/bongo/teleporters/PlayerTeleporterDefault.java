package io.github.noeppi_noeppi.mods.bongo.teleporters;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.compat.SkyblockIntegration;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fml.ModList;

import java.util.List;
import java.util.Random;

public class PlayerTeleporterDefault implements PlayerTeleporter {
    
    public static final PlayerTeleporterDefault INSTANCE = new PlayerTeleporterDefault();
    
    private PlayerTeleporterDefault() {
        
    }
    
    @Override
    public String id() {
        return "bongo.default";
    }

    @Override
    public void teleportTeam(Bongo bongo, ServerLevel gameLevel, Team team, List<ServerPlayer> players, BlockPos center, int radius, Random random) {
        if (ModList.get().isLoaded("skyblockbuilder") && SkyblockIntegration.appliesFor(gameLevel)) {
            SkyblockIntegration.Teleporter.INSTANCE.teleportTeam(bongo, gameLevel, team, players, center, radius, random);
        } else {
            PlayerTeleporterStandard.INSTANCE.teleportTeam(bongo, gameLevel, team, players, center, radius, random);
        }
    }
}
