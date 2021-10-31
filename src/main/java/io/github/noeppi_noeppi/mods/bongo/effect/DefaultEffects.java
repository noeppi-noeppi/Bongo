package io.github.noeppi_noeppi.mods.bongo.effect;

import de.melanx.boohoo.capability.GhostCapability;
import de.melanx.boohoo.capability.IGhostStatus;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStartEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoTaskEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoWinEvent;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import melonslise.spook.common.init.SpookCapabilities;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.*;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.level.GameType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DefaultEffects {

    @SubscribeEvent
    public void gameStart(BongoStartEvent.Level event) {
        event.getLevel().setDayTime(600);
    }

    @SubscribeEvent
    public void playerInit(BongoStartEvent.Player event) {
        event.getPlayer().getInventory().clearContent();
        event.getPlayer().setExperienceLevels(0);
        event.getPlayer().setExperiencePoints(0);
        event.getPlayer().setGameMode(GameType.SURVIVAL);
        event.getBongo().getSettings().fillStartingInventory(event.getPlayer());
        AdvancementCommands.Action.REVOKE.perform(event.getPlayer(), event.getLevel().getServer().getAdvancements().getAllAdvancements());
        ServerStatsCounter mgr = event.getLevel().getServer().getPlayerList().getPlayerStats(event.getPlayer());
        mgr.stats.keySet().forEach(stat -> mgr.stats.put(stat, 0));
        mgr.markAllDirty();
        mgr.sendStats(event.getPlayer());
        TormentData.get(event.getPlayer()).reset();
        event.getPlayer().getCapability(com.cartoonishvillain.eeriehauntings.capabilities.playercapability.PlayerCapability.INSTANCE).ifPresent(cap -> {
            if (cap instanceof com.cartoonishvillain.eeriehauntings.capabilities.playercapability.PlayerCapabilityManager m) {
                m.deserializeNBT(new com.cartoonishvillain.eeriehauntings.capabilities.playercapability.PlayerCapabilityManager().serializeNBT());
            }
        });
        event.getPlayer().getCapability(com.cartoonishvillain.observed.capabilities.PlayerCapability.INSTANCE).ifPresent(cap -> {
            if (cap instanceof com.cartoonishvillain.observed.capabilities.PlayerCapabilityManager m) {
                m.deserializeNBT(new com.cartoonishvillain.observed.capabilities.PlayerCapabilityManager().serializeNBT());
            }
        });
        event.getPlayer().getCapability(GhostCapability.INSTANCE).ifPresent(IGhostStatus::invalidate);
        event.getPlayer().getCapability(SpookCapabilities.SANITY).ifPresent(cap -> cap.set(100));
    }

    @SubscribeEvent
    public void playerTask(BongoTaskEvent event) {
        Team team = event.getBongo().getTeam(event.getPlayer());
        if (team != null) {
            MutableComponent tc = team.getName().append(new TranslatableComponent("bongo.task.complete")).append(event.getTask().getContentName(event.getLevel().getServer()));
            event.getLevel().getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendMessage(tc, player.getUUID());
                if (team.hasPlayer(player)) {
                    player.connection.send(new ClientboundSoundPacket(SoundEvents.END_PORTAL_SPAWN, SoundSource.MASTER, player.getX(), player.getY(), player.getZ(), 0.5f, 1));
                }
            });
        }
    }
    
    @SubscribeEvent
    public void teamWin(BongoWinEvent event) {
        MutableComponent tc = event.getTeam().getName().append(new TranslatableComponent("bongo.win"));
            MutableComponent tcc = event.getTeam().getName().append(new TranslatableComponent("bongo.winplayers"));

            event.getLevel().getServer().getPlayerList().getPlayers().forEach(player -> {
                if (event.getTeam().hasPlayer(player)) {
                    tcc.append(new TextComponent(" "));
                    MutableComponent pname = player.getDisplayName().copy();
                    pname.setStyle(Style.EMPTY.applyFormat(ChatFormatting.RESET).applyFormat(ChatFormatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + player.getX() + " " + player.getY() + " " + player.getZ())));
                    tcc.append(pname);
                }
            });

            event.getLevel().getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendMessage(tcc, player.getUUID());
                player.connection.send(new ClientboundSetTitleTextPacket(tc));
                player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 10));
                player.connection.send(new ClientboundSoundPacket(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundSource.MASTER, player.getX(), player.getY(), player.getZ(), 1.2f, 1));
            });
    }
}
