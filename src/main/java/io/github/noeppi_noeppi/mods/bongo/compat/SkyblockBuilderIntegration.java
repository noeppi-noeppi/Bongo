package io.github.noeppi_noeppi.mods.bongo.compat;

import de.melanx.skyblockbuilder.util.Team;
import de.melanx.skyblockbuilder.util.WorldUtil;
import de.melanx.skyblockbuilder.world.data.SkyblockSavedData;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStartEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStopEvent;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.event.server.FMLServerStartedEvent;

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
}
