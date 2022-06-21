package io.github.noeppi_noeppi.mods.bongo.data.task;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;
import org.moddingx.libx.datapack.DataLoader;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameTasks {
    
    public static final Codec<GameTasks> CODEC = WeightedTaskProvider.CODEC.listOf().fieldOf("tasks").codec()
            .xmap(GameTaskGroup::new, GameTaskGroup::tasks)
            .xmap(GameTasks::new, tasks -> tasks.rootGroup);
    
    private static Map<ResourceLocation, GameTasks> GAME_TASKS = Map.of();
    
    private final GameTaskGroup rootGroup;

    public GameTasks(GameTaskGroup rootGroup) {
        this.rootGroup = rootGroup;
    }

    public Either<List<Task>, String> getBingoTasks() {
        if (rootGroup.availableTasks() < 25) {
            return Either.right("bongo.cmd.create.less");
        }
        return rootGroup.choseTasks(new Random(), 25);
    }
    
    public void validateTasks(MinecraftServer server) {
        rootGroup.validateTasks(server);
    }
    
    public static void validateAllTasks(MinecraftServer server) {
        GAME_TASKS.values().forEach(t -> t.validateTasks(server));
    }

    public static Map<ResourceLocation, GameTasks> gameTasks() {
        return GAME_TASKS;
    }

    public static void loadGameTasks(ResourceManager rm) throws IOException {
        GAME_TASKS = DataLoader.loadJson(rm, "bingo_tasks", CODEC);
    }
}
