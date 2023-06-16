package io.github.noeppi_noeppi.mods.bongo.command;

import com.google.common.base.Suppliers;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.settings.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.task.TaskType;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import org.moddingx.libx.codec.CodecHelper;
import org.moddingx.libx.command.CommandUtil;

import javax.annotation.Nullable;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

public class DumpCommand implements Command<CommandSourceStack> {

    @Override
    public int run(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        try {
            MinecraftServer server = context.getSource().getServer();
            Path base = server.getServerDirectory().toPath().resolve("bongo-dump");
            if (!Files.exists(base)) Files.createDirectories(base);
            if (!Files.exists(base.resolve("bingo_tasks"))) Files.createDirectories(base.resolve("bingo_tasks"));
            if (!Files.exists(base.resolve("bingo_settings"))) Files.createDirectories(base.resolve("bingo_settings"));
            int types = 0;
            
            @Nullable
            ServerPlayer playerForListing = CommandUtil.getArgumentOrDefault(context, "everything", Boolean.class, true) ? null : context.getSource().getPlayerOrException();
            for (TaskType<?> type : TaskTypes.getTypes()) {
                JsonArray data = listElements(type, server, playerForListing);
                JsonObject json = new JsonObject();
                json.add("tasks", data);
                
                Path path = base.resolve("bingo_tasks").resolve(type.id().replace(".", "-") + ".json");
                BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                w.write(BongoMod.PRETTY_GSON.toJson(json));
                w.close();
                types += 1;
            }
            
            JsonElement json = null;
            try {
                json = CodecHelper.JSON.write(GameSettings.CODEC, GameSettings.DEFAULT);
            } catch (Exception e) {
                //
            }
            if (json != null) {
                Path path = base.resolve("bingo_settings").resolve("default.json");
                BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                w.write(BongoMod.PRETTY_GSON.toJson(json));
                w.close();
            }

            context.getSource().sendSuccess(Suppliers.ofInstance(Component.literal("Dumped data for " + types + " task types to " + (base.toAbsolutePath().normalize())).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, base.toAbsolutePath().normalize().toString())).withUnderlined(true))), true);
        } catch (IOException e) {
            throw new SimpleCommandExceptionType(Component.literal("IOException: " + e.getMessage())).create();
        }
        return 0;
    }
    
    private static <T> JsonArray listElements(TaskType<T> type, MinecraftServer server, ServerPlayer player) {
        JsonArray array = new JsonArray();
        type.listElements(server, player)
                .sorted(type.order())
                .map(elem -> new Task(type, elem))
                .flatMap(task -> {
                    try {
                        return Stream.of(CodecHelper.JSON.write(Task.CODEC, task));
                    } catch (Exception e) {
                        e.printStackTrace();
                        return Stream.empty();
                    }
                })
                .forEach(array::add);
        return array;
    }
}
