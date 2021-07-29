package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.fml.ModList;

public class TeamChatCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        if (ModList.get().isLoaded("minemention")) {
            context.getSource().sendSuccess(new TranslatableComponent("bongo.teamchat.disabled"), false);
        }
        Player player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.level);
        Team team = bongo.getTeam(player);

        if (team == null) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.tc.noteam")).create();
        } else if (!bongo.active() || bongo.won()) {
            throw new SimpleCommandExceptionType(new TranslatableComponent("bongo.cmd.tc.norun")).create();
        }

        boolean newState = bongo.toggleTeamChat(player);

        player.sendMessage(new TranslatableComponent("bongo.cmd.tc." + newState), player.getUUID());

        return 0;
    }
}
