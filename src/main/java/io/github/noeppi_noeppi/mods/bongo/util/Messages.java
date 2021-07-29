package io.github.noeppi_noeppi.mods.bongo.util;

import io.github.noeppi_noeppi.libx.util.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Messages {

    public static void onJoin(Level level, Player player, Team team) {
        player.sendMessage(new TranslatableComponent("bongo.cmd.team.joined").append(team.getName()), player.getUUID());
        ServerMessages.broadcastExcept(level, player, ((MutableComponent) player.getDisplayName()).append(new TranslatableComponent("bongo.cmd.team.joinedother").append(team.getName())));
    }

    public static void onLeave(Level level, Player player, Team team) {
        player.sendMessage(new TranslatableComponent("bongo.cmd.team.left").append(team.getName()), player.getUUID());
        ServerMessages.broadcastExcept(level, player, ((MutableComponent) player.getDisplayName()).append(new TranslatableComponent("bongo.cmd.team.leftother").append(team.getName())));
    }
}
