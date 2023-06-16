package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.event.BongoChangeTeamEvent;
import io.github.noeppi_noeppi.mods.bongo.util.Messages;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.common.MinecraftForge;

public class JoinCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.level());
        DyeColor dc = context.getArgument("team", DyeColor.class);

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.noactive")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.running")).create();
        }

        Team oldTeam = bongo.getTeam(player);
        Team team = bongo.getTeam(dc);

        BongoChangeTeamEvent event = new BongoChangeTeamEvent(player, bongo, oldTeam, team, Component.translatable("bongo.cmd.team.denied.join"));
        if (MinecraftForge.EVENT_BUS.post(event)) {
            throw new SimpleCommandExceptionType(event.getFailureMessage()).create();
        } else {
            team.addPlayer(player);
            if (oldTeam != null) {
                Messages.onLeave(player.getCommandSenderWorld(), player, oldTeam);
            }
            Messages.onJoin(player.getCommandSenderWorld(), player, team);
        }

        return 0;
    }
}
