package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class DynamicUtil {
    
    public static <T> DataResult<Dynamic<T>> mergeMaps(Dynamic<T> dynamic, Dynamic<T> additional) {
        AtomicReference<DataResult<Dynamic<T>>> result = new AtomicReference<>(DataResult.success(dynamic));
        additional.getMapValues().result().ifPresent(map -> {
            for (Map.Entry<Dynamic<T>, Dynamic<T>> entry : map.entrySet()) {
                result.updateAndGet(r -> r.flatMap(d -> d.merge(entry.getKey(), entry.getValue()).get()));
            }
        });
        return result.get();
    }
    
    public static <T> Codec<T> createMergedCodec(Codec<T> codec, T defaultValue) {
        return Codec.of(codec, createMergedDecoder(codec, defaultValue));
    }
    
    public static <T> Decoder<T> createMergedDecoder(Codec<T> codec, T defaultValue) {
        return new Decoder<>() {
            
            @Override
            public <A> DataResult<Pair<T, A>> decode(DynamicOps<A> ops, A input) {
                DataResult<Dynamic<A>> dflRes = codec.encodeStart(ops, defaultValue).map(res -> new Dynamic<>(ops, res));
                if (dflRes.result().isEmpty()) {
                    return DataResult.error("Failed to encode default value: " + dflRes.error().map(DataResult.PartialResult::message).orElse("null") + ": " + defaultValue);
                } else {
                    Dynamic<A> dynamic = dflRes.result().get();
                    DataResult<Dynamic<A>> merged = mergeMaps(dynamic, new Dynamic<>(ops, input));
                    //noinspection Convert2MethodRef
                    return merged.flatMap(r -> codec.decode(r));
                }
            }
        };
    }
}
