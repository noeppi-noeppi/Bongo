package io.github.noeppi_noeppi.mods.bongo.command;

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
import net.minecraft.command.CommandSource;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.event.ClickEvent;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public class DumpCommand implements Command<CommandSource> {

    @Override
    public int run(CommandContext<CommandSource> context) throws CommandSyntaxException {
        try {
            MinecraftServer server = context.getSource().getServer();
            Path base = server.getDataDirectory().toPath().resolve("bongo-dump");
            if (!Files.exists(base)) Files.createDirectories(base);
            int types = 0;
            for (TaskType<?> type : TaskTypes.getTypes()) {
                ListNBT data = new ListNBT();
                type.getAllElements(server, CommandUtil.getArgumentOrDefault(context, "everything", Boolean.class, true) ? null : context.getSource().asPlayer()).forEach(obj -> {
                    //noinspection unchecked
                    CompoundNBT taskNbt = ((TaskType<Object>) type).serializeNBT(obj);
                    taskNbt.putString("type", type.getId());
                    data.add(taskNbt);
                });

                JsonObject json = new JsonObject();
                json.add("settings", NbtToJson.getJson(GameSettings.DEFAULT.getTag(), true));
                json.add("tasks", NbtToJson.getJson(data, false));
                Path path = base.resolve(type.getId().replace(".", "-") + ".json");
                BufferedWriter w = Files.newBufferedWriter(path, StandardOpenOption.TRUNCATE_EXISTING, StandardOpenOption.CREATE);
                w.write(BongoMod.PRETTY_GSON.toJson(json));
                w.close();
                types += 1;
            }

            context.getSource().sendFeedback(new StringTextComponent("Dumped data for " + types + " task types to " + (base.toAbsolutePath().normalize().toString())).mergeStyle(Style.EMPTY.setClickEvent(new ClickEvent(ClickEvent.Action.OPEN_FILE, base.toAbsolutePath().normalize().toString())).setUnderlined(true)), true);
        } catch (IOException e) {
            throw new SimpleCommandExceptionType(new StringTextComponent("IOException: " + e.getMessage())).create();
        }
        return 0;
    }
}
