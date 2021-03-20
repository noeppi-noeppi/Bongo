package io.github.noeppi_noeppi.mods.bongo.compat;

import de.melanx.skyblockbuilder.util.Team;
import de.melanx.skyblockbuilder.util.WorldUtil;
import de.melanx.skyblockbuilder.world.data.SkyblockSavedData;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStartEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStopEvent;
import io.github.noeppi_noeppi.mods.bongo.registries.BongoPlayerTeleporter;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

import java.util.List;
import java.util.Random;

public class SkyblockBuilderIntegration {
    @SubscribeEvent
    public void onBongoStart(FMLServerStartedEvent event) {
        ServerWorld world = event.getServer().func_241755_D_();

        if (!WorldUtil.isSkyblock(world)) {
            return;
        }

        SkyblockSavedData data = SkyblockSavedData.get(world);
        for (DyeColor color : DyeColor.values()) {
            data.createTeam(color.name());
        }
    }

    @SubscribeEvent
    public void onJoinTeam(BongoStartEvent event) {
        if (!WorldUtil.isSkyblock(event.getWorld())) {
            return;
        }

        Bongo bongo = event.getBongo();
        SkyblockSavedData data = SkyblockSavedData.get(event.getWorld());

        bongo.getTeams().forEach(team -> {
            String name = team.color.name();
            if (!data.teamExists(name)) {
                data.createTeam(name);
            }
        });
    }

    @SubscribeEvent
    public void onBongoStop(BongoStopEvent event) {
        if (!WorldUtil.isSkyblock(event.getWorld())) {
            return;
        }

        SkyblockSavedData data = SkyblockSavedData.get(event.getWorld());
        Team spawn = data.getSpawn();
        event.getBongo().getTeams().forEach(team -> {
            team.getPlayers().forEach(uuid -> {
                ServerPlayerEntity player = event.getWorld().getServer().getPlayerList().getPlayerByUUID(uuid);
                if (player != null) {
                    WorldUtil.teleportToIsland(player, spawn);
                }
            });
        });
    }

    private static class Teleporter extends BongoPlayerTeleporter {
        @Override
        public void teleportTeam(Bongo bongo, ServerWorld gameWorld, io.github.noeppi_noeppi.mods.bongo.data.Team team, List<ServerPlayerEntity> players, BlockPos center, int radius, Random random) {
            SkyblockSavedData data = SkyblockSavedData.get(gameWorld);
            PlayerList playerList = gameWorld.getServer().getPlayerList();
            Team skyTeam = data.getTeam(team.color.name());
            team.getPlayers().forEach(id -> {
                assert skyTeam != null;
                skyTeam.addPlayer(id);

                ServerPlayerEntity player = playerList.getPlayerByUUID(id);
                if (player != null) {
                    WorldUtil.teleportToIsland(player, skyTeam);
                }
            });
        }
    }
}
