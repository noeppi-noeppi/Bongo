package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.network.chat.Component;
import org.moddingx.libx.command.CommandUtil;
import org.moddingx.libx.util.game.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.server.level.ServerLevel;

public class CreateCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        Bongo bongo = Bongo.get(level);
        if (bongo.running()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.create.running")).create();
        }

        GameTasks gt = context.getArgument("tasks", GameTasks.class);
        GameSettings[] gs = CommandUtil.getArgumentOrDefault(context, "settings", GameSettings[].class, new GameSettings[]{});
        GameDef gd = new GameDef(gt, GameSettings.createCustom(gs));
        bongo.stop();
        bongo.reset();
        String err = gd.createBongo(bongo);
        if (err != null) {
            throw new SimpleCommandExceptionType(Component.translatable(err)).create();
        }
        bongo.activate();

        ServerMessages.broadcast(level, Component.translatable("bongo.info").append(context.getSource().getDisplayName()).append(Component.translatable("bongo.cmd.create.done")));

        return 0;
    }
}
