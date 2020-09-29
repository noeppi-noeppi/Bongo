package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntitySelector;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.text.TranslationTextComponent;

public class TeleportCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        ServerPlayerEntity player = context.getSource().asPlayer();
        Bongo bongo = Bongo.get(player.getEntityWorld());
        Team team = bongo.getTeam(player);

        if (!bongo.running()) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.tp.noactive")).create();
        } else if (team == null) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.tp.noteam")).create();
        }

        EntitySelector sel = context.getArgument("target", EntitySelector.class);
        ServerPlayerEntity target = sel.selectOnePlayer(context.getSource());

        if (target.getGameProfile().getId().equals(player.getGameProfile().getId())) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.tp.self")).create();
        } else if (!team.hasPlayer(target)) {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.tp.wrongteam")).create();
        }

        if (team.consumeTeleport()) {
            if (player.getServerWorld() != target.getServerWorld()) {
                player.changeDimension(target.getServerWorld());
            }
            player.setPositionAndUpdate(target.getPosX(), target.getPosY(), target.getPosZ());
            Util.broadcastTeam(player.getServerWorld(), team, new TranslationTextComponent("bongo.cmd.tp.success", player.getDisplayName(), target.getDisplayName()));
        } else {
            throw new SimpleCommandExceptionType(new TranslationTextComponent("bongo.cmd.tp.noleft")).create();
        }

        return 0;
    }
}
