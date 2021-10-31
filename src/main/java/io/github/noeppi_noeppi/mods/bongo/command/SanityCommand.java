package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import melonslise.spook.common.init.SpookCapabilities;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.selector.EntitySelector;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Mth;

import java.util.List;

public class SanityCommand implements Command<CommandSourceStack> {
    
    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        EntitySelector sel = context.getArgument("players", EntitySelector.class);
        float sanity = Mth.clamp(context.getArgument("sanity", Float.class), 0,100);
        List<ServerPlayer> players = sel.findPlayers(context.getSource());
        for (ServerPlayer player : players) {
            player.getCapability(SpookCapabilities.SANITY).ifPresent(cap -> cap.set(sanity));
        }
        context.getSource().sendSuccess(new TextComponent("Sanity level set for " + players.size() + " players."), false);
        return 0;
    }
}
