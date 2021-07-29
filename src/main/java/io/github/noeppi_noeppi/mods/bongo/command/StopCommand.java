package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;

public class StopCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.getCommandSenderWorld());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.stop.notrunning")).create();
        }
        bongo.stop();

        ServerMessages.broadcast(player.getCommandSenderWorld(), new TranslatableComponent("bongo.info").append(player.getDisplayName()).append(new TranslatableComponent("bongo.cmd.stop.done")));

        return 0;
    }
}
