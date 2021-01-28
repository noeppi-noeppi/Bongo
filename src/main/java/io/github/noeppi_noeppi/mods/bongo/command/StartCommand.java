package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.libx.command.CommandUtil;
import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.server.ServerWorld;

import java.util.Random;

public class StartCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        Bongo bongo = Bongo.get(player.getEntityWorld());

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.start.notcreated")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.start.alreadyrunning")).create();
        }
        bongo.start(CommandUtil.getArgumentOrDefault(context, "randomize_positions", Boolean.class, true));

        ServerMessages.broadcast(player.getEntityWorld(), new TranslationTextComponent("bongo.info").append(player.getDisplayName()).append(new TranslationTextComponent("bongo.cmd.start.done")));

        return 0;
    }
}
