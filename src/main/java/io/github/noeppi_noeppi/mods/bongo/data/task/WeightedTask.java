package io.github.noeppi_noeppi.mods.bongo.data.task;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import org.moddingx.libx.codec.MoreCodecs;

import java.util.stream.Stream;

public record WeightedTask(Task task, int weight) implements WeightedTaskProvider {
    
    public static final Codec<WeightedTask> CODEC = MoreCodecs.extend(Task.CODEC, Codec.INT.fieldOf("weight").orElse(1), task -> Pair.of(task.task(), task.weight()), WeightedTask::new);

    @Override
    public int totalWeight() {
        return this.weight();
    }

    @Override
    public Stream<Pair<Either<Task, GameTaskGroup>, Integer>> elements() {
        return Stream.of(Pair.of(Either.left(this.task()), this.weight()));
    }

    @Override
    public Either<WeightedTask, WeightedGroup> decompose() {
        return Either.left(this);
    }
}
