package io.github.noeppi_noeppi.mods.bongo.teleporters;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Random;

public class PlayerTeleporterNothing implements PlayerTeleporter {

    public static final PlayerTeleporterNothing INSTANCE = new PlayerTeleporterNothing();
    
    private PlayerTeleporterNothing() {
        
    }

    @Override
    public String id() {
        return "bongo.no_tp";
    }

    @Override
    public void teleportTeam(Bongo bongo, ServerLevel gameLevel, Team team, List<ServerPlayer> players, BlockPos center, int radius, Random random) {
        BlockPos.MutableBlockPos mpos = new BlockPos.MutableBlockPos(center.getX(), gameLevel.getMaxBuildHeight(), center.getZ());
        while (mpos.getY() > 5 && gameLevel.getBlockState(mpos).isAir()) {
            mpos.move(Direction.DOWN);
        }
        BlockPos pos = mpos.immutable().above();
        players.forEach(player -> {
            if (player.level() != gameLevel) {
                player.teleportTo(gameLevel, center.getX() + 0.5, pos.getY(), center.getZ() + 0.5, player.getYHeadRot(), 0);
            }
            player.setRespawnPosition(gameLevel.dimension(), player.blockPosition(), 0, true, false);
        });
    }
}
