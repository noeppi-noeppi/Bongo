package io.github.noeppi_noeppi.mods.bongo.data.task;

import com.mojang.datafixers.util.Either;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import org.moddingx.libx.codec.MoreCodecs;

import java.util.stream.Stream;

public sealed interface WeightedTaskProvider permits WeightedTask, WeightedGroup {
    
    Codec<WeightedTaskProvider> CODEC = MoreCodecs.lazy(() -> Codec.either(WeightedGroup.CODEC, WeightedTask.CODEC)
            .xmap(either -> Util.<WeightedTaskProvider>join(either.swap()), provider -> provider.decompose().swap())
    );
    
    int totalWeight();
    Stream<Pair<Either<Task, GameTaskGroup>, Integer>> elements();
    Either<WeightedTask, WeightedGroup> decompose();
}
