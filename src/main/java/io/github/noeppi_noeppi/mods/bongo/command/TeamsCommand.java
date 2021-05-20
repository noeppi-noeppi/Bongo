package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.command.CommandSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class TeamsCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        PlayerEntity player = context.getSource().asPlayer();
        World world = context.getSource().getWorld();
        Bongo bongo = Bongo.get(world);

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.team.noactive")).create();
        }

        for (Team team : bongo.getTeams()) {
            if (team.getPlayers().isEmpty())
                continue;

            IFormattableTextComponent tc = new TranslationTextComponent("bongo.cmd.spread.added");
            tc.appendSibling(team.getName()).appendSibling(new StringTextComponent(":"));

            //noinspection ConstantConditions
            world.getServer().getPlayerList().getPlayers().forEach(teamPlayer -> {
                if (team.hasPlayer(teamPlayer)) {
                    tc.appendSibling(new StringTextComponent(" ")).appendSibling(teamPlayer.getDisplayName());
                }
            });

            player.sendMessage(tc, player.getUniqueID());
        }

        return 0;
    }
}
