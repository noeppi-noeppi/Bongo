package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.*;

import java.util.Map;

public class DynamicUtil {
    
    // No deep merge
    public static <T> DataResult<Dynamic<T>> mergeMaps(Dynamic<T> dynamic, Dynamic<T> additional) {
        DataResult<Dynamic<T>> result = DataResult.success(dynamic);
        boolean dynamicIsMap = dynamic.getMapValues().result().isPresent();
        if (dynamicIsMap) {
            DataResult<Map<Dynamic<T>, Dynamic<T>>> additionalMapValues = additional.getMapValues();
            if (additionalMapValues.result().isPresent()) {
                for (Map.Entry<Dynamic<T>, Dynamic<T>> entry : additionalMapValues.result().get().entrySet()) {
                    result = result.flatMap(dyn -> dyn.merge(entry.getKey(), entry.getValue()).get());
                }
            }
        }
        return result;
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
                    return DataResult.error(() -> "Failed to encode default value: " + dflRes.error().map(DataResult.PartialResult::message).orElse("null") + ": " + defaultValue);
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
