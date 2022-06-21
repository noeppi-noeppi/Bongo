package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.BongoChangeManyTeamsEvent;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;
import org.moddingx.libx.util.game.ServerMessages;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class SpreadCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Level level = context.getSource().getLevel();
        Bongo bongo = Bongo.get(level);
        int teams = Mth.clamp(context.getArgument("amount", Integer.class), 1, 16);

        //noinspection ConstantConditions
        List<Player> players = new ArrayList<>(level.getServer().getPlayerList().getPlayers());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.noactive")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.running")).create();
        } else if (teams > players.size()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.spread.less", teams)).create();
        }

        Set<Team> teamSet = Util.PREFERRED_COLOR_ORDER.subList(0, teams).stream().map(bongo::getTeam).collect(Collectors.toSet());
        BongoChangeManyTeamsEvent event = new BongoChangeManyTeamsEvent(bongo, teamSet, Component.translatable("bongo.cmd.team.denied.many"));
        if (MinecraftForge.EVENT_BUS.post(event)) {
            throw new SimpleCommandExceptionType(event.getFailureMessage()).create();
        } else {
            int perTeam = players.size() / teams;
            int teamsWithOneMore = players.size() % teams;

            Random random = new Random();
            for (int i = 0; i < teams; i++) {
                Team team = bongo.getTeam(Util.PREFERRED_COLOR_ORDER.get(i));
                team.clearPlayers();
                int playersThisTeam = i < teamsWithOneMore ? perTeam + 1 : perTeam;
                List<Player> added = new ArrayList<>();
                for (int j = 0; j < playersThisTeam; j++) {
                    Player player = players.remove(random.nextInt(players.size()));
                    team.addPlayer(player);
                    added.add(player);
                }
                MutableComponent tc = Component.translatable("bongo.cmd.spread.added");
                tc.append(team.getName()).append(Component.literal(":"));
                for (Player player : added) {
                    tc.append(Component.literal(" ")).append(player.getDisplayName());
                }
                ServerMessages.broadcast(level, tc);
            }
        }

        return 0;
    }
}
