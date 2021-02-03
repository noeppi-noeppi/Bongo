package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.datafixers.util.Either;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.ResourceLocation;

import java.io.IOException;
import java.util.*;

public class GameTasks { 
    
    public static final Map<ResourceLocation, GameTasks> GAME_TASKS = new HashMap<>();
    
    public final ResourceLocation id;
    private final CompoundNBT nbt;
    
    private final GameTaskGroup rootGroup;
    
    public GameTasks(ResourceLocation id, CompoundNBT nbt) {
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
    
    public CompoundNBT getTag() {
        return nbt;
    }
    
    public static void loadGameTasks(IResourceManager rm) throws IOException {
        GameDef.loadData(rm, "bingo_tasks", GAME_TASKS, GameTasks::new);
    }
}
