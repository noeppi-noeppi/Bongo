package io.github.noeppi_noeppi.mods.bongo.teleporters;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Random;

public class PlayerTeleporterStandard implements PlayerTeleporter {

    public static final PlayerTeleporterStandard INSTANCE = new PlayerTeleporterStandard();
    
    private PlayerTeleporterStandard() {
        
    }

    @Override
    public String id() {
        return "bongo.standard";
    }

    @Override
    public void teleportTeam(Bongo bongo, ServerLevel gameLevel, Team team, List<ServerPlayer> players, BlockPos center, int radius, Random random) {
        BlockPos pos;
        int i = 0;
        do {
            int x = center.getX() + (random.nextInt(2 * radius) - radius);
            int z = center.getZ() + (random.nextInt(2 * radius) - radius);
            BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(x, gameLevel.getMaxBuildHeight(), z);
            while (mpos.getY() > gameLevel.getMinBuildHeight() + 5 && gameLevel.getBlockState(mpos).isAir()) {
                mpos.move(Direction.DOWN);
            }
            pos = mpos.immutable().above();
            i++;
        } while (!Util.validSpawn(gameLevel, pos) && i < 10);
        // After 10 tries we give up and use the last position
        BlockPos effectiveFinalPos = pos;
        players.forEach(player -> {
            player.teleportTo(gameLevel, effectiveFinalPos.getX() + 0.5, effectiveFinalPos.getY(), effectiveFinalPos.getZ() + 0.5, player.getYHeadRot(), 0);
            player.setRespawnPosition(gameLevel.dimension(), effectiveFinalPos, 0, true, false);
        });
    }
}
