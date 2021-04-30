package io.github.noeppi_noeppi.mods.bongo.command;

import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.item.DyeColor;
import net.minecraftforge.event.RegisterCommandsEvent;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static io.github.noeppi_noeppi.libx.command.UppercaseEnumArgument.enumArgument;
import static io.github.noeppi_noeppi.mods.bongo.command.arg.GameSettingsArgument.gameSettings;
import static io.github.noeppi_noeppi.mods.bongo.command.arg.GameTasksArgument.gameTasks;
import static net.minecraft.command.Commands.argument;
import static net.minecraft.command.Commands.literal;

public class BongoCommands {

    public static void register(RegisterCommandsEvent event) {
        event.getDispatcher().register(literal("bp").executes(new BackPackCommand()));
        event.getDispatcher().register(literal("tc").executes(new TeamChatCommand()));

        event.getDispatcher().register(literal("bingo").then(
                literal("backpack").executes(new BackPackCommand())
        ).then(
                literal("join").then(argument("team", enumArgument(DyeColor.class)).executes(new JoinCommand()))
        ).then(
                literal("leave").executes(new LeaveCommand())
        ).then(
                literal("create").requires(cs -> cs.hasPermissionLevel(2)).then(argument("tasks", gameTasks()).executes(new CreateCommand()).then(argument("settings", gameSettings()).executes(new CreateCommand())))
        ).then(
                literal("start").requires(cs -> cs.hasPermissionLevel(2)).executes(new StartCommand())
        ).then(
                literal("stop").requires(cs -> cs.hasPermissionLevel(2)).executes(new StopCommand())
        ).then(
                literal("spread").requires(cs -> cs.hasPermissionLevel(2)).then(argument("amount", integer(1, 16)).executes(new SpreadCommand()))
        ).then(
                literal("teams").executes(new TeamsCommand())
        ).then(
                literal("teamchat").executes(new TeamChatCommand())
        ).then(
                literal("dump").requires(cs -> cs.hasPermissionLevel(2)).executes(new DumpCommand()).then(argument("everything", bool()).executes(new DumpCommand()))
        ).then(
                literal("teleport").then(argument("target", EntityArgument.player()).executes(new TeleportCommand()))
        ).then(
                literal("tp").then(argument("target", EntityArgument.player()).executes(new TeleportCommand()))
        ).then(
                literal("emergency").executes(new EmergencyCommand())
        ));
    }
}
