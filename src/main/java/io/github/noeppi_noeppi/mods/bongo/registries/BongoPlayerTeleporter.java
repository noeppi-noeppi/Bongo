package io.github.noeppi_noeppi.mods.bongo.registries;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.registries.ForgeRegistryEntry;

import java.util.List;
import java.util.Random;

/**
 * Specifies a way how players should be teleported in a world. can be used to alter the
 * behaviour of player teleports. The registered can be set in the game settings.
 * The `teleportTeam` is called when the game starts.
 */
public abstract class BongoPlayerTeleporter extends ForgeRegistryEntry<BongoPlayerTeleporter> {
    
    /**
     * This method should teleport a team in the world.
     * 
     * <b>When subclassing this, be aware that players may not be in the game world when this is called. This
     * means only updating the location is not sufficient if you don't check the world first.</b>
     * 
     * @param bongo The bongo instance
     * @param gameWorld The world where the game should take place
     * @param team The team to teleport
     * @param players The players in the team to teleport
     * @param center The approximate center position of all teams. You should ignore the Y value of this.
     * @param radius Theteleportation radius. This may be ignored by the implementation.
     * @param random A random to be used for random positions.
     */
    public abstract void teleportTeam(Bongo bongo, ServerWorld gameWorld, Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random);
}
