package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.command.event.BongoChangeManyTeamsEvent;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;

import java.util.*;
import java.util.stream.Collectors;

public class SpreadCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        World world = context.getSource().getWorld();
        Bongo bongo = Bongo.get(world);
        int teams = MathHelper.clamp(context.getArgument("amount", Integer.class), 1, 16);

        //noinspection ConstantConditions
        List<PlayerEntity> players = new ArrayList<>(world.getServer().getPlayerList().getPlayers());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.team.noactive")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.team.running")).create();
        } else if (teams > players.size()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.spread.less", teams)).create();
        }

        Set<Team> teamSet = Util.PREFERRED_COLOR_ORDER.subList(0, teams).stream().map(bongo::getTeam).collect(Collectors.toSet());
        BongoChangeManyTeamsEvent event = new BongoChangeManyTeamsEvent(bongo, teamSet, new TranslationTextComponent("bongo.cmd.team.denied.many"));
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
                List<PlayerEntity> added = new ArrayList<>();
                for (int j = 0; j < playersThisTeam; j++) {
                    PlayerEntity player = players.remove(random.nextInt(players.size()));
                    team.addPlayer(player);
                    added.add(player);
                }
                IFormattableTextComponent tc = new TranslationTextComponent("bongo.cmd.spread.added");
                tc.append(team.getName()).append(new StringTextComponent(":"));
                for (PlayerEntity player : added) {
                    tc.append(new StringTextComponent(" ")).append(player.getDisplayName());
                }
                ServerMessages.broadcast(world, tc);
            }
        }

        return 0;
    }
}
