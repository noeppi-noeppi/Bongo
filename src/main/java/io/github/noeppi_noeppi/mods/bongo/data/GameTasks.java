package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.datafixers.util.Either;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.packs.resources.ResourceManager;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class GameTasks { 
    
    public static final Map<ResourceLocation, GameTasks> GAME_TASKS = new HashMap<>();
    
    public final ResourceLocation id;
    private final CompoundTag nbt;
    
    private final GameTaskGroup rootGroup;
    
    public GameTasks(ResourceLocation id, CompoundTag nbt) {
        this.id = id;
        this.rootGroup = GameTaskGroup.parseRootTasks(nbt);
        this.nbt = nbt.copy();
    }
    
    public Either<List<Task>, String> getBingoTasks() {
        if (rootGroup.getAvailableTasks() < 25) {
            return Either.right("bongo.cmd.create.less");
        }
        return rootGroup.choseTasks(new Random(), 25);
    }
    
    public CompoundTag getTag() {
        return nbt;
    }
    
    public void validateTasks(MinecraftServer server) {
        rootGroup.validateTasks(server);
    }
    
    public static void loadGameTasks(ResourceManager rm) throws IOException {
        GameDef.loadData(rm, "bingo_tasks", GAME_TASKS, GameTasks::new);
    }
    
    public static void validateAllTasks(MinecraftServer server) {
        for (GameTasks tasks : GAME_TASKS.values()) {
            tasks.validateTasks(server);
        }
    }
}
