package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import io.github.noeppi_noeppi.mods.bongo.data.settings.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.task.GameTasks;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import org.moddingx.libx.command.CommandUtil;
import org.moddingx.libx.util.game.ServerMessages;

import java.util.List;
import java.util.NoSuchElementException;

public class CreateCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        ServerLevel level = context.getSource().getLevel();
        Bongo bongo = Bongo.get(level);
        if (bongo.running()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.create.running")).create();
        }

        ResourceLocation tasksId = context.getArgument("tasks", ResourceLocation.class);
        //noinspection unchecked
        List<ResourceLocation> settingIds = CommandUtil.getArgumentOrDefault(context, "settings", (Class<List<ResourceLocation>>) (Class<?>) List.class, List.of());

        GameTasks tasks = GameTasks.gameTasks().get(tasksId);
        if (tasks == null) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.create.notfound")).create();
        }

        GameSettings settings;
        try {
            settings = GameSettings.load(settingIds);
        } catch (NoSuchElementException e) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.create.notfound")).create();
        } catch (Exception e) {
            e.printStackTrace();
            throw new SimpleCommandExceptionType(Component.literal("Error while merging settings: " + e.getMessage())).create();
        }
        
        GameDef gd = new GameDef(tasks, settings);
        
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
