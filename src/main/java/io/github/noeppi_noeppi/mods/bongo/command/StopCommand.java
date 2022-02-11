package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;

public class StopCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bongo bongo = Bongo.get(context.getSource().getLevel());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.stop.notrunning")).create();
        }
        bongo.stop();

        ServerMessages.broadcast(context.getSource().getLevel(), new TranslatableComponent("bongo.info").append(context.getSource().getDisplayName()).append(new TranslatableComponent("bongo.cmd.stop.done")));

        return 0;
    }
}
