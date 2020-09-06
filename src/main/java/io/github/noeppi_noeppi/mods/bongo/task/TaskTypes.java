package io.github.noeppi_noeppi.mods.bongo.task;

import java.util.HashMap;
import java.util.Map;

public class TaskTypes {

    private static final Map<String, TaskType<?>> taskTypes = new HashMap<>();

    public static TaskType<?> getType(String id) {
        if (id == null)
            return null;
        return taskTypes.get(id.toLowerCase());
    }

    public static void registerType(TaskType<?> type) {
        String id = type.getId().toLowerCase();
        if (taskTypes.containsKey(id)) {
            throw new IllegalStateException("TaskType with id '" + id + "' is already registered.");
        } else {
            taskTypes.put(id, type);
        }
    }
}
