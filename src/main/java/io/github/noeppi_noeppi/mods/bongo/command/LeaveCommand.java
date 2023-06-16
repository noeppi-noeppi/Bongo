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
import net.minecraftforge.common.MinecraftForge;

public class LeaveCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.level());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.noactive")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.running")).create();
        }

        Team team = bongo.getTeam(player);
        if (team == null) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.leavonojoin")).create();
        }
        
        BongoChangeTeamEvent event = new BongoChangeTeamEvent(player, bongo, team, null, Component.translatable("bongo.cmd.team.denied.leave"));
        if (MinecraftForge.EVENT_BUS.post(event)) {
            throw new SimpleCommandExceptionType(event.getFailureMessage()).create();
        } else {
            team.removePlayer(player);
            Messages.onLeave(player.getCommandSenderWorld(), player, team);
        }

        return 0;
    }
}
