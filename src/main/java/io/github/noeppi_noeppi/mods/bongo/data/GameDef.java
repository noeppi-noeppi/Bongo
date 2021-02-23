package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.datafixers.util.Either;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.resources.IResource;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.BiFunction;

public class GameDef {

    public final GameTasks tasks;
    public final GameSettings settings;

    public GameDef(GameTasks tasks, GameSettings settings) {
        this.tasks = tasks;
        this.settings = settings;
    }

    public String createBongo(Bongo bongo) {
        if (bongo.running()) {
            bongo.stop();
        }
        Either<List<Task>, String> taskList = tasks.getBingoTasks();
        if (taskList.right().isPresent() || !taskList.left().isPresent()) {
            return taskList.right().isPresent() ? taskList.right().get() : "Unknown Error";
        }
        bongo.setSettings(settings, true);
        bongo.setTasks(taskList.left().get());
        return null;
    }

    public static <T> void loadData(IResourceManager rm, String path, Map<ResourceLocation, T> map, BiFunction<ResourceLocation, CompoundNBT, T> factory) throws IOException {
        map.clear();

        Collection<ResourceLocation> ids = rm.getAllResourceLocations(path, file -> file.endsWith(".json"));

        for (ResourceLocation id : ids) {
            String realPath;
            if (id.getPath().contains("/")) {
                realPath = id.getPath().substring(id.getPath().lastIndexOf('/') + 1);
            } else {
                realPath = id.getPath();
            }
            if (realPath.endsWith(".json"))
                realPath = realPath.substring(0, realPath.length() - 5);

            ResourceLocation realId = new ResourceLocation(id.getNamespace(), realPath);

            IResource resource = rm.getResource(id);

            String string = IOUtils.toString(new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8));
            CompoundNBT nbt;
            try {
                nbt = JsonToNBT.getTagFromJson(string);
            } catch (CommandSyntaxException e) {
                throw new IOException("Could not read JSON-NBT: " + e.getMessage(), e);
            }
            map.put(realId, factory.apply(realId, nbt));
        }
    }
}
