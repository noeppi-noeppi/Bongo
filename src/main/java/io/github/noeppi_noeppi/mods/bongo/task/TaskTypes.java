package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.MapCodec;
import org.moddingx.libx.codec.CodecHelper;

import javax.annotation.Nullable;
import java.util.*;

public class TaskTypes {

    public static final Codec<TaskType<?>> CODEC = Codec.STRING.flatXmap(
            id -> CodecHelper.nonNull(getType(id), "Task type not found: " + id),
            type -> DataResult.success(type.id())
    );
    
    private static final Map<String, TaskType<?>> taskTypes = new HashMap<>();
    private static final Map<TaskType<?>, Codec<?>> taskCodecs = new HashMap<>();
    
    @Nullable
    public static TaskType<?> getType(String id) {
        if (id == null) return null;
        return taskTypes.get(id.toLowerCase(Locale.ROOT));
    }
    
    public static DataResult<Codec<?>> getCodec(TaskType<?> type) {
        Codec<?> codec = type == null ? null : taskCodecs.get(type);
        return codec == null ? DataResult.error(() -> "Task type not found: " + (type == null ? "null" : type.id())) : DataResult.success(codec);
    }

    public static <T> void registerType(TaskType<T> type) {
        String id = type.id().toLowerCase(Locale.ROOT);
        if (taskTypes.containsKey(id)) {
            throw new IllegalStateException("TaskType with id '" + id + "' is already registered.");
        } else {
            MapCodec<T> codec = type.codec();
            List<String> forbiddenKeys = codec.keys(JsonOps.INSTANCE)
                    .filter(Task.RESERVED_KEYS::contains)
                    .map(j -> j.isJsonPrimitive() ? j.getAsString() : j.toString())
                    .toList();
            if (!forbiddenKeys.isEmpty()) {
                throw new IllegalStateException("Task type can't declare reserved keys: [ " + String.join(", ", forbiddenKeys) + " ]");
            }
            
            taskTypes.put(id, type);
            taskCodecs.put(type, codec.codec());
        }
    }

    public static Collection<TaskType<?>> getTypes() {
        return taskTypes.values();
    }
}
