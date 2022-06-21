package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import org.moddingx.libx.codec.CodecHelper;

import javax.annotation.Nullable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

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
        return codec == null ? DataResult.error("Task type not found: " + (type == null ? "null" : type.id())) : DataResult.success(codec);
    }

    public static <T> void registerType(TaskType<T> type) {
        String id = type.id().toLowerCase(Locale.ROOT);
        if (taskTypes.containsKey(id)) {
            throw new IllegalStateException("TaskType with id '" + id + "' is already registered.");
        } else {
            taskTypes.put(id, type);
            taskCodecs.put(type, type.codec().codec());
        }
    }

    public static Collection<TaskType<?>> getTypes() {
        return taskTypes.values();
    }
}
