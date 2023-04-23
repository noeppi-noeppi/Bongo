package io.github.noeppi_noeppi.mods.bongo.data.task;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import net.minecraft.server.MinecraftServer;

import java.util.*;

public class GameTaskGroup {
    
    public static final String PSEUDO_TYPE = "bongo.group";
    
    // Must fail if type is not bongo.group as used in an either codec.
    public static final Codec<GameTaskGroup> CODEC = RecordCodecBuilder.<Pair<String, GameTaskGroup>>create(instance -> instance.group(
            Codec.STRING.fieldOf("type").forGetter(Pair::getFirst), 
            WeightedTaskProvider.CODEC.listOf().fieldOf("tasks").forGetter(pair -> pair.getSecond().tasks())
    ).apply(instance, (type, tasks) -> Pair.of(type, new GameTaskGroup(tasks)))).flatXmap(
            pair -> PSEUDO_TYPE.equals(pair.getFirst()) ? DataResult.success(pair.getSecond()) : DataResult.error(() -> "Not a task group"),
            group -> DataResult.success(Pair.of(PSEUDO_TYPE, group))
    );
    
    private final List<WeightedTaskProvider> tasks;
    private final List<Pair<Either<Task, GameTaskGroup>, Integer>> builtTasks;
    private final int totalWeight;
    
    public GameTaskGroup(List<WeightedTaskProvider> tasks) {
        this.tasks = List.copyOf(tasks);
        this.builtTasks = this.tasks.stream().flatMap(WeightedTaskProvider::elements).toList();
        this.totalWeight = this.tasks.stream().map(WeightedTaskProvider::totalWeight).reduce(0, Integer::sum);
    }

    public int totalWeight() {
        return totalWeight;
    }

    public int availableTasks() {
        return builtTasks.size();
    }

    public List<WeightedTaskProvider> tasks() {
        return tasks;
    }

    public Either<List<Task>, String> choseTasks(Random random, int taskAmount) {
        if (builtTasks.size() < taskAmount) {
            return Either.right("bongo.cmd.create.less.subgroup");
        }

        int weightLeft = totalWeight;
        int collectedTasks = 0;
        List<Task> theTasks = new ArrayList<>();
        Map<GameTaskGroup, Integer> theGroups = new HashMap<>();
        List<Integer> theTasksIndices = new ArrayList<>();
        while (collectedTasks < taskAmount) {
            int rand = random.nextInt(weightLeft);
            int weightCounted = 0;
            for (int i = 0; i < builtTasks.size(); i++) {
                if (!theTasksIndices.contains(i)) {
                    Pair<Either<Task, GameTaskGroup>, Integer> pair = builtTasks.get(i);
                    weightCounted += pair.getSecond();
                    if (weightCounted > rand) {
                        Either<Task, GameTaskGroup> taskDef = pair.getFirst();
                        if (taskDef.left().isPresent()) {
                            theTasks.add(taskDef.left().get());
                        } else if (taskDef.right().isPresent()) {
                            GameTaskGroup group = taskDef.right().get();
                            if (!theGroups.containsKey(group)) {
                                theGroups.put(group, 0);
                            }
                            theGroups.put(group, theGroups.get(group) + 1);
                        }
                        theTasksIndices.add(i);
                        collectedTasks += 1;
                        weightLeft -= pair.getSecond();
                        break;
                    }
                }
            }
        }
        for (Map.Entry<GameTaskGroup, Integer> entry : theGroups.entrySet()) {
            if (entry.getValue() > 0) {
                Either<List<Task>, String> result = entry.getKey().choseTasks(random, entry.getValue());
                if (result.right().isPresent() || result.left().isEmpty()) {
                    return Either.right(result.right().isPresent() ? result.right().get() : "Unknown Error");
                } else {
                    theTasks.addAll(result.left().get());
                }
            }
        }
        // If this was not here rare items would be more likely to appear in the first row.
        Collections.shuffle(theTasks);
        return Either.left(theTasks);
    }
    
    public void validateTasks(MinecraftServer server) {
        for (WeightedTaskProvider entry : this.tasks) {
            Either<Task, GameTaskGroup> task = entry.decompose()
                    .mapLeft(WeightedTask::task)
                    .mapRight(WeightedGroup::group);
            task.left().ifPresent(t -> t.validate(server));
            task.right().ifPresent(t -> t.validateTasks(server));
        }
    }
}
