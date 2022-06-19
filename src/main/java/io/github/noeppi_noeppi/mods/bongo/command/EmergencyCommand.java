package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;

public class EmergencyCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        Player player = context.getSource().getPlayerOrException();
        Bongo bongo = Bongo.get(player.level);
        Team team = bongo.getTeam(player);
        
        if (team == null) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.emergency.noteam")).create();
        } else if (!bongo.running()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.emergency.norun")).create();
        } else if (!bongo.getSettings().hasEmergencyItems()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.emergency.disabled")).create();
        } else if (team.redeemedEmergency()) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.emergency.duplicate")).create();
        } else if (!team.lockRandomTasks(3)) {
            throw new SimpleCommandExceptionType(Component.translatable("bongo.cmd.emergency.lesstasks")).create();
        } else {
            bongo.getSettings().giveEmergencyItems(player);
            team.redeemedEmergency(true);
            player.sendSystemMessage(Component.translatable("bongo.cmd.emergency.redeemed"));
        }
        
        return 0;
    }
}
