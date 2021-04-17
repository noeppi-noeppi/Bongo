package io.github.noeppi_noeppi.mods.bongo.teleporters;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class PlayerTeleporterStandard implements PlayerTeleporter {

    public static final PlayerTeleporterStandard INSTANCE = new PlayerTeleporterStandard();
    
    private PlayerTeleporterStandard() {
        
    }

    @Override
    public String getId() {
        return "bongo.standard";
    }

    @Override
    public void teleportTeam(Bongo bongo, ServerWorld gameWorld, Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random) {
        BlockPos pos;
        int i = 0;
        do {
            int x = center.getX() + (random.nextInt(2 * radius) - radius);
            int z = center.getZ() + (random.nextInt(2 * radius) - radius);
            BlockPos.Mutable mpos = new BlockPos.Mutable(x, gameWorld.getHeight(), z);
            //noinspection deprecation
            while (mpos.getY() > 5 && gameWorld.getBlockState(mpos).isAir(gameWorld, mpos)) {
                mpos.move(Direction.DOWN);
            }
            pos = mpos.toImmutable().up();
            i++;
        } while (!Util.validSpawn(gameWorld, pos) && i < 10);
        // After 10 tries we give up and use the last position
        BlockPos effectiveFinalPos = pos;
        players.forEach(player -> {
            player.teleport(gameWorld, effectiveFinalPos.getX() + 0.5, effectiveFinalPos.getY(), effectiveFinalPos.getZ() + 0.5, player.getRotationYawHead(), 0);
            player.func_242111_a(gameWorld.getDimensionKey(), effectiveFinalPos, 0, true, false);
        });
    }
}
