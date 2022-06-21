package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.Map;

public class DynamicUtil {
    
    public static <T> void mergeMaps(Dynamic<T> dynamic, Dynamic<T> additional) {
        additional.getMapValues().result().ifPresent(map -> {
            for (Map.Entry<Dynamic<T>, Dynamic<T>> entry : map.entrySet()) {
                dynamic.merge(entry.getKey(), entry.getValue());
            }
        });
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
                    mergeMaps(dynamic, new Dynamic<>(ops, input));
                    return codec.decode(dynamic);
                }
            }
        };
    }
}
