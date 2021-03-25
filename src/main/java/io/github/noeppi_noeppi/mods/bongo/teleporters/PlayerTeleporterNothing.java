package io.github.noeppi_noeppi.mods.bongo.teleporters;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;

import java.util.List;
import java.util.Random;

public class PlayerTeleporterNothing implements PlayerTeleporter {

    public static final PlayerTeleporterNothing INSTANCE = new PlayerTeleporterNothing();
    
    private PlayerTeleporterNothing() {
        
    }

    @Override
    public String getId() {
        return "bongo.no_tp";
    }

    @Override
    public void teleportTeam(Bongo bongo, ServerWorld gameWorld, Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random) {
        BlockPos.Mutable mpos = new BlockPos.Mutable(center.getX(), gameWorld.getHeight(), center.getZ());
        //noinspection deprecation
        while (mpos.getY() > 5 && gameWorld.getBlockState(mpos).isAir(gameWorld, mpos)) {
            mpos.move(Direction.DOWN);
        }
        BlockPos pos = mpos.toImmutable().up();
        players.forEach(player -> {
            if (player.world != gameWorld) {
                player.teleport(gameWorld, center.getX() + 0.5, pos.getY(), center.getZ() + 0.5, player.getRotationYawHead(), 0);
            }
            player.func_242111_a(gameWorld.getDimensionKey(), player.getPosition(), 0, true, false);
        });
    }
}
