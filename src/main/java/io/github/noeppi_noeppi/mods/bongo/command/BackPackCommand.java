package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class BackPackCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.level());
        Team team = bongo.getTeam(player);

        if (team == null) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.bp.noteam")).create();
        } else if (!bongo.running()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.bp.norun")).create();
        }

        team.openBackPack(player);

        return 0;
    }
}
