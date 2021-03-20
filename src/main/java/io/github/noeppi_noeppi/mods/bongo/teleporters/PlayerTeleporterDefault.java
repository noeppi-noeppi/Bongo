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
        int x = center.getX() + (random.nextInt(2 * radius) - radius);
        int z = center.getZ() + (random.nextInt(2 * radius) - radius);
        BlockPos.Mutable mpos = new BlockPos.Mutable(x, gameWorld.getHeight(), z);
        //noinspection deprecation
        while (mpos.getY() > 5 && gameWorld.getBlockState(mpos).isAir(gameWorld, mpos)) {
            mpos.move(Direction.DOWN);
        }
        BlockPos pos = mpos.toImmutable().up();
        players.forEach(player -> player.teleport(gameWorld, pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getRotationYawHead(), 0));
    }
}
