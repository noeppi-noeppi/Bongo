package io.github.noeppi_noeppi.mods.bongo.effect;

import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.data.settings.KeptLevelData;
import io.github.noeppi_noeppi.mods.bongo.event.BongoStartEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoTaskEvent;
import io.github.noeppi_noeppi.mods.bongo.event.BongoWinEvent;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket;
import net.minecraft.network.protocol.game.ClientboundSetTitlesAnimationPacket;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;
import net.minecraft.server.commands.AdvancementCommands;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.ServerStatsCounter;
import net.minecraft.world.level.GameType;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class DefaultEffects {

    @SubscribeEvent
    public void gameStart(BongoStartEvent.Level event) {
        Set<KeptLevelData> keep = event.getBongo().getSettings().level().keep();
        if (!keep.contains(KeptLevelData.time)) {
            event.getLevel().setDayTime(600);
        }
        if (!keep.contains(KeptLevelData.weather)) {
            event.getLevel().serverLevelData.setRaining(false);
            event.getLevel().serverLevelData.setThundering(false);
        }
        if (!keep.contains(KeptLevelData.wandering_trader_time)) {
            event.getLevel().serverLevelData.setWanderingTraderSpawnDelay(24000);
            event.getLevel().serverLevelData.setWanderingTraderSpawnChance(25);
        }
    }

    @SubscribeEvent
    public void playerInit(BongoStartEvent.Player event) {
        Set<KeptLevelData> keep = event.getBongo().getSettings().level().keep();
        if (!keep.contains(KeptLevelData.equipment)) {
            event.getPlayer().getInventory().clearContent();
            event.getBongo().getSettings().equipment().equip(event.getPlayer());
        }
        if (!keep.contains(KeptLevelData.experience)) {
            event.getPlayer().setExperienceLevels(0);
            event.getPlayer().setExperiencePoints(0);
        }
        if (!keep.contains(KeptLevelData.game_mode)) {
            event.getPlayer().setGameMode(GameType.SURVIVAL);
        }
        if (!keep.contains(KeptLevelData.advancements)) {
            AdvancementCommands.Action.REVOKE.perform(event.getPlayer(), event.getLevel().getServer().getAdvancements().getAllAdvancements());
        }
        if (!keep.contains(KeptLevelData.statistics)) {
            ServerStatsCounter mgr = event.getLevel().getServer().getPlayerList().getPlayerStats(event.getPlayer());
            mgr.stats.keySet().forEach(stat -> mgr.stats.put(stat, 0));
            mgr.markAllDirty();
            mgr.sendStats(event.getPlayer());
        }
    }

    @SubscribeEvent
    public void playerTask(BongoTaskEvent event) {
        Team team = event.getBongo().getTeam(event.getPlayer());
        if (team != null) {
            MutableComponent tc = Component.translatable(event.getTask().inverted() ? "bongo.task.fail" : "bongo.task.complete", team.getName(), event.getTask().contentName(event.getLevel().getServer()));
            event.getLevel().getServer().getPlayerList().getPlayers().forEach(player -> {
                player.sendSystemMessage(tc);
                if (team.hasPlayer(player)) {
                    player.connection.send(new ClientboundSoundPacket(Util.sound(SoundEvents.END_PORTAL_SPAWN), SoundSource.MASTER, player.getX(), player.getY(), player.getZ(), 0.5f, 1, 0));
                }
            });
        }
    }

    @SubscribeEvent
    public void teamWin(BongoWinEvent event) {
        MutableComponent tc = event.getTeam().getName().append(Component.translatable("bongo.win"));
        MutableComponent tcc = event.getTeam().getName().append(Component.translatable("bongo.winplayers"));

        event.getLevel().getServer().getPlayerList().getPlayers().forEach(player -> {
            if (event.getTeam().hasPlayer(player)) {
                tcc.append(Component.literal(" "));
                MutableComponent pname = player.getDisplayName().copy();
                pname.setStyle(Style.EMPTY.applyFormat(ChatFormatting.RESET).applyFormat(ChatFormatting.UNDERLINE).withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tp @p " + player.getX() + " " + player.getY() + " " + player.getZ())));
                tcc.append(pname);
            }
        });

        MutableComponent leaderboard;
        if (!event.getBongo().getSettings().game().leaderboard()) {
            leaderboard = null;
        } else {
            leaderboard = Component.empty();
            // retrieve a sorted list of all teams in order of completed tasks amount descending
            List<Team> teams = event.getBongo().getTeams().stream()
                    .filter(team -> !team.getPlayers().isEmpty())
                    .sorted(Comparator.comparingInt((Team team) -> team.completion().count()).reversed())
                    .toList();
            int place = 0; // the current placement
            int toSkip = 0; // the amount of places to skip (because of equal amount of completed tasks)
            int prevTeam = -1; // the amount of tasks the previous team had
            for (Team team : teams) {
                int completed = team.completion().count();
                toSkip += 1;
                if (prevTeam != completed) {
                    place += toSkip;
                    toSkip = 0;
                }
                leaderboard.append(Component.literal(" "));
                leaderboard.append(Component.translatable(
                        completed == 1 ? "bongo.completed_tasks.one" : "bongo.completed_tasks.multiple",
                        Integer.toString(place), team.getName(), Integer.toString(completed)
                ));
                leaderboard.append("\n");
                prevTeam = completed;
            }
        }
        
        event.getLevel().getServer().getPlayerList().getPlayers().forEach(player -> {
            player.sendSystemMessage(tcc);
            player.connection.send(new ClientboundSetTitleTextPacket(tc));
            player.connection.send(new ClientboundSetTitlesAnimationPacket(10, 60, 10));
            player.connection.send(new ClientboundSoundPacket(Util.sound(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE), SoundSource.MASTER, player.getX(), player.getY(), player.getZ(), 1.2f, 1, 0));
            if (leaderboard != null) {
                player.sendSystemMessage(leaderboard);
            }
        });
    }
}
