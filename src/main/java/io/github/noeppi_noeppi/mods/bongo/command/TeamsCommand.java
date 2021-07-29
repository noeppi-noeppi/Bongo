package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TeamsCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Level level = context.getSource().getLevel();
        Bongo bongo = Bongo.get(level);

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.team.noactive")).create();
        }

        for (Team team : bongo.getTeams()) {
            if (team.getPlayers().isEmpty())
                continue;

            MutableComponent tc = new TranslatableComponent("bongo.cmd.spread.added");
            tc.append(team.getName()).append(new TextComponent(":"));

            //noinspection ConstantConditions
            level.getServer().getPlayerList().getPlayers().forEach(teamPlayer -> {
                if (team.hasPlayer(teamPlayer)) {
                    tc.append(new TextComponent(" ")).append(teamPlayer.getDisplayName());
                }
            });

            player.sendMessage(tc, player.getUUID());
        }

        return 0;
    }
}
