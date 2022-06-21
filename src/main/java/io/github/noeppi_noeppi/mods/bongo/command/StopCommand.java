package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import org.moddingx.libx.util.game.ServerMessages;

public class StopCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Bongo bongo = Bongo.get(context.getSource().getLevel());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.stop.notrunning")).create();
        }
        bongo.stop();

        ServerMessages.broadcast(context.getSource().getLevel(), Component.translatable("bongo.info").append(context.getSource().getDisplayName()).append(Component.translatable("bongo.cmd.stop.done")));

        return 0;
    }
}
