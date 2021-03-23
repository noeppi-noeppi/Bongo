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
import net.minecraftforge.fml.ModList;

public class TeamChatCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        if (ModList.get().isLoaded("minemention")) {
            context.getSource().sendFeedback(new TranslationTextComponent("bongo.teamchat.disabled"), false);
        }
        PlayerEntity player = context.getSource().asPlayer();
        Bongo bongo = Bongo.get(player.world);
        Team team = bongo.getTeam(player);

        if (team == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.tc.noteam")).create();
        } else if (!bongo.active() || bongo.won()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.tc.norun")).create();
        }

        boolean newState = bongo.toggleTeamChat(player);

        player.sendMessage(new TranslationTextComponent("bongo.cmd.tc." + newState), player.getUniqueID());

        return 0;
    }
}
