package io.github.noeppi_noeppi.mods.bongo.command;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import io.github.noeppi_noeppi.libx.command.CommandUtil;
import io.github.noeppi_noeppi.libx.util.NbtToJson;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.task.TaskType;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypes;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Comparator;
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
            for (TaskType<?, ?> type : TaskTypes.getTypes()) {
                ListTag data = new ListTag();
                Stream<?> stream = type.getAllElements(server, CommandUtil.getArgumentOrDefault(context, "everything", Boolean.class, true) ? null : context.getSource().getPlayerOrException());
                Comparator<?> comparator = type.getSorting();
                if (comparator != null) {
                    //noinspection unchecked
                    stream = stream.sorted((Comparator<Object>) comparator);
                }
                stream.forEach(obj -> {
                    //noinspection unchecked
                    CompoundTag taskNbt = ((TaskType<Object, ?>) type).serializeNBT(obj);
                    taskNbt.putString("type", type.getId());
                    data.add(taskNbt);
                });

                JsonObject json = new JsonObject();
                json.add("tasks", NbtToJson.getJson(data, false));
                Path path = base.resolve("bingo_tasks").resolve(type.getId().replace(".", "-") + ".json");
                BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                w.write(BongoMod.PRETTY_GSON.toJson(json));
                w.close();
                types += 1;
            }
            JsonElement json = NbtToJson.getJson(GameSettings.DEFAULT.getTag(), true);
            Path path = base.resolve("bingo_settings").resolve("default.json");
            BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
            w.write(BongoMod.PRETTY_GSON.toJson(json));
            w.close();

            context.getSource().sendSuccess(new TextComponent("Dumped data for " + types + " task types to " + (base.toAbsolutePath().normalize())).withStyle(Style.EMPTY.withClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, base.toAbsolutePath().normalize().toString())).setUnderlined(true)), true);
        } catch (IOException e) {
            throw new SimpleCommandExceptionType(new TextComponent("IOException: " + e.getMessage())).create();
        }
        return 0;
    }
}
