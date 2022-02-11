package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.libx.command.CommandUtil;
import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import io.github.noeppi_noeppi.mods.bongo.data.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.GameTasks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;

public class CreateCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        Bongo bongo = Bongo.get(level);
        if (bongo.running()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.create.running")).create();
        }

        GameTasks gt = context.getArgument("tasks", GameTasks.class);
        GameSettings[] gs = CommandUtil.getArgumentOrDefault(context, "settings", GameSettings[].class, new GameSettings[]{});
        GameDef gd = new GameDef(gt, GameSettings.createCustom(gs));
        bongo.stop();
        bongo.reset();
        String err = gd.createBongo(bongo);
        if (err != null) {
            throw new SimpleCommandExceptionType(new TranslatableComponent(err)).create();
        }
        bongo.activate();

        ServerMessages.broadcast(level, new TranslatableComponent("bongo.info").append(context.getSource().getDisplayName()).append(new TranslatableComponent("bongo.cmd.create.done")));

        return 0;
    }
}
