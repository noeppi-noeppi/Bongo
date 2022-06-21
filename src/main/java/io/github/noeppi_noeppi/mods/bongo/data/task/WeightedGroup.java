package io.github.noeppi_noeppi.mods.bongo.data.task;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import org.moddingx.libx.codec.MoreCodecs;

import java.util.List;
import java.util.stream.Stream;

public record WeightedGroup(GameTaskGroup group, List<Integer> weights) implements WeightedTaskProvider {

    private static final Codec<List<Integer>> WEIGHTS_CODEC = Codec.either(Codec.INT, Codec.INT.listOf()).xmap(either -> Util.join(either.mapLeft(List::of)), Either::right);

    public static final Codec<WeightedGroup> CODEC = MoreCodecs.extend(GameTaskGroup.CODEC, WEIGHTS_CODEC.fieldOf("weight").orElse(List.of(1)), group -> Pair.of(group.group(), group.weights()), WeightedGroup::new);

    @Override
    public int totalWeight() {
        return weights.stream().reduce(0, Integer::sum);
    }

    @Override
    public Stream<Pair<Either<Task, GameTaskGroup>, Integer>> elements() {
        return weights.stream().map(weight -> Pair.of(Either.right(this.group()), weight));
    }

    @Override
    public Either<WeightedTask, WeightedGroup> decompose() {
        return Either.right(this);
    }
}
