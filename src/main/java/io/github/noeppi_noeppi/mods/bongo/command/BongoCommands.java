package io.github.noeppi_noeppi.mods.bongo.command;

import com.mojang.brigadier.arguments.FloatArgumentType;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.world.item.DyeColor;
import net.minecraftforge.event.RegisterCommandsEvent;

import static com.mojang.brigadier.arguments.BoolArgumentType.bool;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static io.github.noeppi_noeppi.libx.command.EnumArgument2.enumArgument;
import static io.github.noeppi_noeppi.mods.bongo.command.arg.GameSettingsArgument.gameSettings;
import static io.github.noeppi_noeppi.mods.bongo.command.arg.GameTasksArgument.gameTasks;
import static net.minecraft.commands.Commands.argument;
import static net.minecraft.commands.Commands.literal;

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
                literal("create").requires(cs -> cs.hasPermission(2)).then(argument("tasks", gameTasks()).executes(new CreateCommand()).then(argument("settings", gameSettings()).executes(new CreateCommand())))
        ).then(
                literal("start").requires(cs -> cs.hasPermission(2)).executes(new StartCommand())
        ).then(
                literal("stop").requires(cs -> cs.hasPermission(2)).executes(new StopCommand())
        ).then(
                literal("spread").requires(cs -> cs.hasPermission(2)).then(argument("amount", integer(1, 16)).executes(new SpreadCommand()))
        ).then(
                literal("teams").executes(new TeamsCommand())
        ).then(
                literal("teamchat").executes(new TeamChatCommand())
        ).then(
                literal("dump").requires(cs -> cs.hasPermission(2)).executes(new DumpCommand()).then(argument("everything", bool()).executes(new DumpCommand()))
        ).then(
                literal("teleport").then(argument("target", EntityArgument.player()).executes(new TeleportCommand()))
        ).then(
                literal("tp").then(argument("target", EntityArgument.player()).executes(new TeleportCommand()))
        ).then(
                literal("emergency").executes(new EmergencyCommand())
        ));
        
        event.getDispatcher().register(literal("sanity")
                        .requires(cs -> cs.hasPermission(2))
                        .then(argument("players", EntityArgument.players())
                                .then(argument("sanity", FloatArgumentType.floatArg(0, 100))
                                        .executes(new SanityCommand()))));
    }
}
