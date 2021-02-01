package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.datafixers.util.Either;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraftforge.common.util.Constants;
import org.apache.commons.lang3.tuple.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class GameTaskGroup {
    
    public static final String PSEUDO_TYPE = "bongo.group";
    
    private final List<Pair<Integer, Either<Task, GameTaskGroup>>> tasks;
    private final int totalWeight;
    
    private GameTaskGroup(ListNBT taskList) {
        tasks = new ArrayList<>();
        int totalWeight = 0;
        for (int i = 0; i < taskList.size(); i++) {
            CompoundNBT compound = taskList.getCompound(i);
            int weight = compound.getInt("weight");
            if (weight <= 0) weight = 1;
            
            Either<Task, GameTaskGroup> task = parseTask(compound);
            
            tasks.add(Pair.of(weight, task));
            totalWeight += weight;
        }
        this.totalWeight = totalWeight;
    }
    
    public Either<List<Task>, String> choseTasks(Random random, int taskAmount) {
        if (tasks.size() < taskAmount) {
            return Either.right("bongo.cmd.create.less");
        }

        int weightLeft = totalWeight;
        List<Task> theTasks = new ArrayList<>();
        List<Integer> theTasksIndices = new ArrayList<>();
        while (theTasks.size() < taskAmount) {
            int rand = random.nextInt(weightLeft);
            int weightCounted = 0;
            for (int i = 0; i < tasks.size(); i++) {
                if (!theTasksIndices.contains(i)) {
                    Pair<Integer, Either<Task, GameTaskGroup>> pair = tasks.get(i);
                    weightCounted += pair.getLeft();
                    if (weightCounted > rand) {
                        Either<Task, GameTaskGroup> taskDef = pair.getRight();
                        if (taskDef.left().isPresent()) {
                            theTasks.add(taskDef.left().get());
                        } else if (taskDef.right().isPresent()) {
                            Either<List<Task>, String> result = taskDef.right().get().choseTasks(random, 1);
                            if (result.right().isPresent() || !result.left().isPresent()) {
                                return Either.right(result.right().isPresent() ? result.right().get() : "Unknown Error");
                            }
                            theTasks.add(result.left().get().get(0));
                        }
                        theTasksIndices.add(i);
                        weightLeft -= pair.getLeft();
                        break;
                    }
                }
            }
        }
        // If this was not here rare items would be more likely to appear in the first row.
        Collections.shuffle(theTasks);
        return Either.left(theTasks);
    }
    
    public static Either<Task, GameTaskGroup> parseTask(CompoundNBT nbt) {
        if (PSEUDO_TYPE.equals(nbt.getString("type"))) {
            ListNBT tasks = nbt.getList("tasks", Constants.NBT.TAG_COMPOUND);
            if (tasks.size() == 1) {
                return parseTask(tasks.getCompound(0));
            } else {
                return Either.right(new GameTaskGroup(tasks));
            }
        } else {
            Task task = Task.empty();
            task.deserializeNBT(nbt);
            return Either.left(task);
        }
    }
    
    public static GameTaskGroup parseRootTasks(CompoundNBT nbt) {
        ListNBT tasks = nbt.getList("tasks", Constants.NBT.TAG_COMPOUND);
        return new GameTaskGroup(tasks);
    }
}
