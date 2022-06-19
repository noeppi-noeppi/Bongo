package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.network.chat.Component;
import org.moddingx.libx.util.game.ServerMessages;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class Messages {

    public static void onJoin(Level level, Player player, Team team) {
        player.sendSystemMessage(Component.translatable("bongo.cmd.team.joined").append(team.getName()));
        ServerMessages.broadcastTo(level, p -> p != player, ((MutableComponent) player.getDisplayName()).append(Component.translatable("bongo.cmd.team.joinedother").append(team.getName())));
    }

    public static void onLeave(Level level, Player player, Team team) {
        player.sendSystemMessage(Component.translatable("bongo.cmd.team.left").append(team.getName()));
        ServerMessages.broadcastTo(level, p -> p != player, ((MutableComponent) player.getDisplayName()).append(Component.translatable("bongo.cmd.team.leftother").append(team.getName())));
    }
}
