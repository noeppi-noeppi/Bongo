package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class TeamsCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Level level = context.getSource().getLevel();
        Bongo bongo = Bongo.get(level);

        if (!bongo.active()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.team.noactive")).create();
        }

        for (Team team : bongo.getTeams()) {
            if (team.getPlayers().isEmpty())
                continue;

            MutableComponent tc = Component.translatable("bongo.cmd.spread.added");
            tc.append(team.getName()).append(Component.literal(":"));

            //noinspection ConstantConditions
            level.getServer().getPlayerList().getPlayers().forEach(teamPlayer -> {
                if (team.hasPlayer(teamPlayer)) {
                    tc.append(Component.literal(" ")).append(teamPlayer.getDisplayName());
                }
            });

            player.sendSystemMessage(tc);
        }

        return 0;
    }
}
