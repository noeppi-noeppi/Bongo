package io.github.noeppi_noeppi.mods.bongo.registries;

import de.melanx.skyblockbuilder.util.WorldUtil;
import de.melanx.skyblockbuilder.world.data.SkyblockSavedData;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class SkyblockTeleporter extends BongoPlayerTeleporter {

    public static final SkyblockTeleporter INSTANCE = new SkyblockTeleporter();

    private SkyblockTeleporter() {
        this.setRegistryName(new ResourceLocation(BongoMod.getInstance().modid, "skyblock"));
    }

    @Override
    public void teleportTeam(Bongo bongo, ServerWorld gameWorld, Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random) {
        if (!WorldUtil.isSkyblock(gameWorld)) {
            throw new IllegalStateException("You shouldn't use this in non-skyblock worlds.");
        }

        SkyblockSavedData data = SkyblockSavedData.get(gameWorld);
        de.melanx.skyblockbuilder.util.Team skyTeam = data.getTeam(team.color.name());
        players.forEach(player -> {
            assert skyTeam != null;
            skyTeam.addPlayer(player);
            WorldUtil.teleportToIsland(player, skyTeam);
        });
    }
}
