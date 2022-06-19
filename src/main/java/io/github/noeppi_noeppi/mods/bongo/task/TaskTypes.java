package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import org.moddingx.libx.codec.CodecHelper;
import org.moddingx.libx.codec.MoreCodecs;

import javax.annotation.Nullable;
import java.util.*;

public class TaskTypes {

    public static final Codec<TaskType<?>> CODEC = Codec.STRING.flatXmap(
            id -> CodecHelper.nonNull(getType(id), "Task type not found: " + id),
            type -> DataResult.success(type.id())
    );
    
    private static final Map<String, TaskType<?>> taskTypes = new HashMap<>();
    private static final Map<TaskType<?>, Codec<Task>> taskCodecs = new HashMap<>();
    
    @Nullable
    public static TaskType<?> getType(String id) {
        if (id == null) return null;
        return taskTypes.get(id.toLowerCase(Locale.ROOT));
    }
    
    public static DataResult<Codec<Task>> getCodec(TaskType<?> type) {
        Codec<Task> codec = type == null ? null : taskCodecs.get(type);
        return codec == null ? DataResult.error("Task type not found: " + (type == null ? "null" : type.id())) : DataResult.success(codec);
    }

    public static <T> void registerType(TaskType<T> type) {
        String id = type.id().toLowerCase(Locale.ROOT);
        if (taskTypes.containsKey(id)) {
            throw new IllegalStateException("TaskType with id '" + id + "' is already registered.");
        } else {
            taskTypes.put(id, type);
            //noinspection unchecked
            taskCodecs.put(type, RecordCodecBuilder.create(instance -> instance.group(
                    TaskTypes.CODEC.fieldOf("type").forGetter(t -> type),
                    type.codec().forGetter((Task task) -> Objects.requireNonNull(task.getElement(type), "Task has invalid type"))
            ).apply(instance, (TaskType<?> t, T elem) -> new Task((TaskType<T>) t, elem))));
        }
    }

    public static Collection<TaskType<?>> getTypes() {
        return taskTypes.values();
    }
}
