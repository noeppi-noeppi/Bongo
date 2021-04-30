package io.github.noeppi_noeppi.mods.bongo.compat;

import de.melanx.skyblockbuilder.data.SkyblockSavedData;
import de.melanx.skyblockbuilder.util.CompatHelper;
import de.melanx.skyblockbuilder.util.WorldUtil;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStopEvent;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporter;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.*;

public class SkyblockIntegration {

    public static void init() {
        MinecraftForge.EVENT_BUS.register(new SkyblockIntegration.Events());
        CompatHelper.disableAllTeamManagement(BongoMod.getInstance().modid);
    }
    
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
            Optional<de.melanx.skyblockbuilder.data.Team> spawn = data.getSpawnOption();
            if (!spawn.isPresent()) {
                return;
            }

            Arrays.stream(DyeColor.values()).forEach(color -> {
                de.melanx.skyblockbuilder.data.Team island = data.getTeam("bongo_" + color.getTranslationKey());
                if (island != null && data.deleteTeam(island)) {
                    for (ServerPlayerEntity player : event.getWorld().getServer().getPlayerList().getPlayers()) {
                        if (island.hasPlayer(player)) WorldUtil.teleportToIsland(player, spawn.get());
                    }
                }
            });
        }
        
        @SubscribeEvent(priority = EventPriority.HIGH)
        public void livingHurt(LivingHurtEvent event) {
            if (event.getEntityLiving() instanceof ServerPlayerEntity && event.getSource().canHarmInCreative()
                    && event.getEntityLiving().getPosY() < 0
                    && World.OVERWORLD.equals(event.getEntityLiving().getEntityWorld().getDimensionKey())) {
                ServerPlayerEntity player = ((ServerPlayerEntity) event.getEntityLiving());
                if (appliesFor(player.getServerWorld())) {
                    Bongo bongo = Bongo.get(player.getServerWorld());
                    if (bongo.running()) {
                        BlockPos pos = player.func_241140_K_();
                        if (World.OVERWORLD.equals(player.func_241141_L_()) && pos != null) {
                            event.setCanceled(true);
                            Util.handleTaskLocking(bongo, player);
                            player.teleport(player.getServerWorld(), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5,
                                    player.rotationYaw, player.rotationPitch);
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
