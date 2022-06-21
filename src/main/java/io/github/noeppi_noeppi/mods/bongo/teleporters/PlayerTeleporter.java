package io.github.noeppi_noeppi.mods.bongo.teleporters;

import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;
import java.util.Random;

/**
 * Specifies a way how players should be teleported in a world. can be used to alter the
 * behaviour of player teleports. The registered can be set in the game settings.
 * The `teleportTeam` is called when the game starts.
 */
public interface PlayerTeleporter {

    String id();
    
    /**
     * This method should teleport a team in the world.
     * 
     * <b>When subclassing this, be aware that players may not be on the game level when this is called. This
     * means only updating the location is not sufficient if you don't check the world first.</b>
     * 
     * @param bongo The bongo instance
     * @param gameLevel The world where the game should take place
     * @param team The team to teleport
     * @param players The players in the team to teleport
     * @param center The approximate center position of all teams. You should ignore the Y value of this.
     * @param radius The teleportation radius. This may be ignored by the implementation.
     * @param random A random to be used for random positions.
     */
    void teleportTeam(Bongo bongo, ServerLevel gameLevel, Team team, List<ServerPlayer> players, BlockPos center, int radius, Random random);
}
