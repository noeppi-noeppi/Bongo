package io.github.noeppi_noeppi.mods.bongo.util;

import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

public class Messages {

    public static void onJoin(World world, PlayerEntity player, Team team) {
        player.sendMessage(new TranslationTextComponent("bongo.cmd.team.joined").append(team.getName()), player.getUniqueID());
        Util.broadcastExcept(world, player, ((IFormattableTextComponent) player.getDisplayName()).append(new TranslationTextComponent("bongo.cmd.team.joinedother").append(team.getName())));
    }

    public static void onLeave(World world, PlayerEntity player, Team team) {
        player.sendMessage(new TranslationTextComponent("bongo.cmd.team.left").append(team.getName()), player.getUniqueID());
        Util.broadcastExcept(world, player, ((IFormattableTextComponent) player.getDisplayName()).append(new TranslationTextComponent("bongo.cmd.team.leftother").append(team.getName())));
    }
}
