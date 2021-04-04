package io.github.noeppi_noeppi.mods.bongo.compat;

import de.melanx.skyblockbuilder.data.SkyblockSavedData;
import de.melanx.skyblockbuilder.events.*;
import de.melanx.skyblockbuilder.util.WorldUtil;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStopEvent;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

public class SkyblockIntegration {
    
    public static boolean appliesFor(ServerWorld world) {
        try {
            return WorldUtil.isSkyblock(world);
        } catch (Exception | NoClassDefFoundError e) {
            return false;
        }
    }
    
    public static class Events {
        
        @SubscribeEvent
        public void onStop(BongoStopEvent.World event) {
            // Delete all skyblock teams that were created.
            SkyblockSavedData data = SkyblockSavedData.get(event.getWorld());
            de.melanx.skyblockbuilder.data.Team spawn = data.getSpawn();
            Arrays.stream(DyeColor.values()).forEach(color -> {
                de.melanx.skyblockbuilder.data.Team island = data.getTeam("bongo_" + color.getTranslationKey());
                if (island != null && data.deleteTeam(island)) {
                    for (ServerPlayerEntity player : event.getWorld().getServer().getPlayerList().getPlayers()) {
                        if (island.hasPlayer(player)) WorldUtil.teleportToIsland(player, spawn);
                    }
                }
            });
        }
        
        @SubscribeEvent
        public void createTeam(SkyblockCreateTeamEvent event) {
            event.setCanceled(true);
        }
        
        @SubscribeEvent
        public void invitePlayer(SkyblockInvitationEvent event) {
            event.setResult(Event.Result.DENY);
        }
        
        @SubscribeEvent
        public void manageTeam(SkyblockManageTeamEvent event) {
            event.setResult(Event.Result.DENY);
        }
        
        @SubscribeEvent
        public void opManage(SkyblockOpManageEvent event) {
            event.setCanceled(true);
        }
        
        @SubscribeEvent
        public void home(SkyblockTeleportHomeEvent event) {
            event.setResult(Event.Result.DENY);
        }
        
        @SubscribeEvent
        public void visit(SkyblockVisitEvent event) {
            event.setResult(Event.Result.DENY);
        }
    }
    
    public static class Teleporter implements PlayerTeleporter {

        public static final Teleporter INSTANCE = new Teleporter();
        
        private Teleporter() {
            
        }

        @Override
        public String getId() {
            return "bongo.skyblock";
        }

        @Override
        public void teleportTeam(Bongo bongo, ServerWorld gameWorld, Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random) {
            SkyblockSavedData data = SkyblockSavedData.get(gameWorld);
            de.melanx.skyblockbuilder.data.Team island = data.getTeam("bongo_" + team.color.getTranslationKey());
            if (island == null) island = data.createTeam("bongo_" + team.color.getTranslationKey());
            Objects.requireNonNull(island);
            for (ServerPlayerEntity player : players) {
                island.addPlayer(player);
                WorldUtil.teleportToIsland(player, island);
            }
        }
    }
}
