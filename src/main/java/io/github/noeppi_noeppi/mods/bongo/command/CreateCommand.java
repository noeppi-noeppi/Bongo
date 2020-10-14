package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class CreateCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        World world = player.world;
        Bongo bongo = Bongo.get(world);
        if (bongo.running()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.create.running")).create();
        }

        GameDef gd = context.getArgument("pattern", GameDef.class);
        bongo.stop();
        bongo.reset();
        String err = gd.createBongo(bongo);
        if (err != null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent(err)).create();
        }
        bongo.activate();

        ServerMessages.broadcast(world, new TranslationTextComponent("bongo.info").append(player.getDisplayName()).append(new TranslationTextComponent("bongo.cmd.create.done")));

        return 0;
    }
}
