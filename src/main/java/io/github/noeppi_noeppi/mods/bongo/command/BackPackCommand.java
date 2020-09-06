package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class BackPackCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        Bongo bongo = Bongo.get(player.world);
        Team team = bongo.getTeam(player);

        if (team == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.bp.noteam")).create();
        } else if (!bongo.running()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.bp.norun")).create();
        }

        team.openBackPack(player);

        return 0;
    }
}
