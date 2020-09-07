package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.util.Messages;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.DyeColor;
import net.minecraft.util.text.TranslationTextComponent;

public class JoinCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        Bongo bongo = Bongo.get(player.world);
        DyeColor dc = context.getArgument("team", DyeColor.class);

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.team.noactive")).create();
        } else if (bongo.running() || bongo.won()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.team.running")).create();
        }

        Team oldTeam = bongo.getTeam(player);

        Team team = bongo.getTeam(dc);
        team.addPlayer(player);

        if (oldTeam != null) {
            Messages.onLeave(player.getEntityWorld(), player, oldTeam);
        }
        Messages.onJoin(player.getEntityWorld(), player, team);

        return 0;
    }
}
