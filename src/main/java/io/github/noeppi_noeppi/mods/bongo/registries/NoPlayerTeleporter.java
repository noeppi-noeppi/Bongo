package io.github.noeppi_noeppi.mods.bongo.registries;

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

public class NoPlayerTeleporter extends BongoPlayerTeleporter {

    public static final NoPlayerTeleporter INSTANCE = new NoPlayerTeleporter();
    
    private NoPlayerTeleporter() {
        this.setRegistryName(new ResourceLocation(BongoMod.getInstance().modid, "no_tp"));
    }
    
    @Override
    public void teleportTeam(Bongo bongo, ServerWorld gameWorld, Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random) {
        BlockPos.Mutable mpos = new BlockPos.Mutable(center.getX(), gameWorld.getHeight(), center.getZ());
        //noinspection deprecation
        while (mpos.getY() > 5 && gameWorld.getBlockState(mpos).isAir(gameWorld, mpos)) {
            mpos.move(Direction.DOWN);
        }
        BlockPos pos = mpos.toImmutable().up();
        players.forEach(player -> player.teleport(gameWorld, center.getX() + 0.5, pos.getY(), center.getZ() + 0.5, player.getRotationYawHead(), 0));
    }
}
