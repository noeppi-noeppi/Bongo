package io.github.noeppi_noeppi.mods.bongo.compat;

import de.melanx.skyblockbuilder.api.SkyblockBuilderAPI;
import de.melanx.skyblockbuilder.data.SkyblockSavedData;
import de.melanx.skyblockbuilder.util.WorldUtil;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStopEvent;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporter;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class SkyblockIntegration {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new SkyblockIntegration.Events());
    }

    public static void setup() {
        SkyblockBuilderAPI.disableAllTeamManagement(BongoMod.getInstance().modid);
    }
    
    public static boolean appliesFor(ServerLevel level) {
        try {
            return WorldUtil.isSkyblock(level);
        } catch (Exception | NoClassDefFoundError e) {
            return false;
        }
    }

    public static class Events {

        @SubscribeEvent
        public void onStop(BongoStopEvent.Level event) {
            // Delete all skyblock teams that were created.
            SkyblockSavedData data = SkyblockSavedData.get(event.getLevel());
            Optional<de.melanx.skyblockbuilder.data.Team> spawn = data.getSpawnOption();
            if (spawn.isEmpty()) {
                return;
            }

            Arrays.stream(DyeColor.values()).forEach(color -> {
                de.melanx.skyblockbuilder.data.Team island = data.getTeam("bongo_" + color.getName());
                if (island != null && data.deleteTeam(island.getId())) {
                    for (ServerPlayer player : event.getLevel().getServer().getPlayerList().getPlayers()) {
                        if (island.hasPlayer(player)) WorldUtil.teleportToIsland(player, spawn.get());
                    }
                }
            });
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public void livingHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof ServerPlayer player && event.getSource().isBypassInvul()
                    && event.getEntityLiving().getY() < 0
                    && Level.OVERWORLD.equals(event.getEntityLiving().getCommandSenderWorld().dimension())) {
                if (appliesFor(player.getLevel())) {
                    Bongo bongo = Bongo.get(player.getLevel());
                    if (bongo.running()) {
                        BlockPos pos = player.getRespawnPosition();
                        if (Level.OVERWORLD.equals(player.getRespawnDimension()) && pos != null) {
                            event.setCanceled(true);
                            Util.handleTaskLocking(bongo, player);
                            player.teleportTo(player.getLevel(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, player.getYRot(), player.getXRot());
                        }
                    }
                }
            }
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
        public void teleportTeam(Bongo bongo, ServerLevel gameLevel, Team team, List<ServerPlayer> players, BlockPos center, int radius, Random random) {
            SkyblockSavedData data = SkyblockSavedData.get(gameLevel);
            de.melanx.skyblockbuilder.data.Team island = data.getTeam("bongo_" + team.color.getName());
            if (island == null) island = data.createTeam("bongo_" + team.color.getName());
            Objects.requireNonNull(island);
            for (ServerPlayer player : players) {
                island.addPlayer(player);
                WorldUtil.teleportToIsland(player, island);
            }
        }
    }
}
