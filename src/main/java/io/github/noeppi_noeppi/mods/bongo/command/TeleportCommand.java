package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerPlayer;

public class TeleportCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerPlayer player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.getCommandSenderWorld());
        Team team = bongo.getTeam(player);

        if (!bongo.running()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.tp.noactive")).create();
        } else if (team == null) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.tp.noteam")).create();
        }

        EntitySelector sel = context.getArgument("target", EntitySelector.class);
        ServerPlayer target = sel.findSinglePlayer(context.getSource());

        if (target.getGameProfile().getId().equals(player.getGameProfile().getId())) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.tp.self")).create();
        } else if (!team.hasPlayer(target)) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.tp.wrongteam")).create();
        }

        if (team.consumeTeleport()) {
            if (player.getLevel() != target.getLevel()) {
                player.changeDimension(target.getLevel());
            }
            player.teleportTo(target.getX(), target.getY(), target.getZ());
            Util.broadcastTeam(player.getLevel(), team, new TranslatableComponent("bongo.cmd.tp.success", player.getDisplayName(), target.getDisplayName()));
        } else {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.tp.noleft")).create();
        }

        return 0;
    }
}
