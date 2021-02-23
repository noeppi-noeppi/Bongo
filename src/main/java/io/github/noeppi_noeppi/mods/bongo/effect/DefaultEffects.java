package io.github.noeppi_noeppi.mods.bongo.effect;

import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStartEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoTaskEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoWinEvent;
import net.minecraft.command.impl.AdvancementCommand;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import net.minecraft.network.play.server.STitlePacket;
import net.minecraft.stats.ServerStatisticsManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.text.*;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class DefaultEffects {

    @SubscribeEvent
    public void gameStart(BongoStartEvent.World event) {
        event.getWorld().func_241114_a_(600);
    }

    @SubscribeEvent
    public void playerInit(BongoStartEvent.Player event) {
        event.getPlayer().inventory.clear();
        event.getBongo().getSettings().fillStartingInventory(event.getPlayer());
        AdvancementCommand.Action.REVOKE.applyToAdvancements(event.getPlayer(), event.getWorld().getServer().getAdvancementManager().getAllAdvancements());
        ServerStatisticsManager mgr = event.getWorld().getServer().getPlayerList().getPlayerStats(event.getPlayer());
        mgr.statsData.keySet().forEach(stat -> mgr.statsData.put(stat, 0));
        mgr.markAllDirty();
        mgr.sendStats(event.getPlayer());
    }

    @SubscribeEvent
    public void playerTask(BongoTaskEvent event) {
        Team team = event.getBongo().getTeam(event.getPlayer());
        if (team != null) {
            IFormattableTextComponent tc = team.getName().append(new TranslationTextComponent("bongo.task.complete")).append(event.getTask().getContentName(event.getWorld().getServer()));
            event.getWorld().getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendMessage(tc, player.getUniqueID());
                if (team.hasPlayer(player)) {
                    player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.BLOCK_END_PORTAL_SPAWN, SoundCategory.MASTER, player.getPosX(), player.getPosY(), player.getPosZ(), 0.5f, 1));
                }
            });
        }
    }
    
    @SubscribeEvent
    public void teamWin(BongoWinEvent event) {
        IFormattableTextComponent tc = event.getTeam().getName().append(new TranslationTextComponent("bongo.win"));
            IFormattableTextComponent tcc = event.getTeam().getName().append(new TranslationTextComponent("bongo.winplayers"));

            event.getWorld().getServer().getPlayerList().getPlayers().forEach(player -> {
                if (event.getTeam().hasPlayer(player)) {
                    tcc.append(new StringTextComponent(" "));
                    IFormattableTextComponent pname = player.getDisplayName().deepCopy();
                    pname.setStyle(Style.EMPTY.applyFormatting(TextFormatting.RESET).applyFormatting(TextFormatting.UNDERLINE).setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + player.getPosX() + " " + player.getPosY() + " " + player.getPosZ())));
                    tcc.append(pname);
                }
            });

            event.getWorld().getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendMessage(tcc, player.getUniqueID());
                player.connection.sendPacket(new STitlePacket(STitlePacket.Type.TITLE, tc, 10, 60, 10));
                player.connection.sendPacket(new SPlaySoundEffectPacket(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE, SoundCategory.MASTER, player.getPosX(), player.getPosY(), player.getPosZ(), 1.2f, 1));
            });
    }
}
