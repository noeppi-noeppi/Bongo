package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import io.github.noeppi_noeppi.mods.bongo.command.arg.GameDefArgument;
import io.github.noeppi_noeppi.mods.bongo.command.arg.UppercaseEnumArgument;
import net.minecraft.command.Commands;
import net.minecraft.item.DyeColor;
import net.minecraftforge.event.RegisterCommandsEvent;

public class BongoCommands {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("bp").executes(new BackPackCommand()));
        event.getDispatcher().register(Commands.literal("tc").executes(new TeamChatCommand()));

        event.getDispatcher().register(Commands.literal("bingo").then(
                Commands.literal("backpack").executes(new BackPackCommand())
        ).then(
                Commands.literal("join").then(Commands.argument("team", UppercaseEnumArgument.enumArgument(DyeColor.class)).executes(new JoinCommand()))
        ).then(
                Commands.literal("leave").executes(new LeaveCommand())
        ).then(
                Commands.literal("create").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("pattern", GameDefArgument.gameDef()).executes(new CreateCommand()))
        ).then(
                Commands.literal("start").requires(cs -> cs.hasPermissionLevel(2)).executes(new StartCommand()).then(Commands.argument("randomize_positions", BoolArgumentType.bool()).executes(new StartCommand()))
        ).then(
                Commands.literal("stop").requires(cs -> cs.hasPermissionLevel(2)).executes(new StopCommand())
        ).then(
                Commands.literal("spread").requires(cs -> cs.hasPermissionLevel(2)).then(Commands.argument("amount", IntegerArgumentType.integer(1, 16)).executes(new SpreadCommand()))
        ).then(
                Commands.literal("teams").executes(new TeamsCommand())
        ).then(
                Commands.literal("teamchat").executes(new TeamChatCommand())
        ));
    }
}
