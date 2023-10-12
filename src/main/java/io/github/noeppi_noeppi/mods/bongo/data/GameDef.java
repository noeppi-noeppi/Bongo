package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.datafixers.util.Either;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.settings.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.task.GameTasks;
import io.github.noeppi_noeppi.mods.bongo.task.Task;

import java.util.List;

public class GameDef {

    public final GameTasks tasks;
    public final GameSettings settings;

    public GameDef(GameTasks tasks, GameSettings settings) {
        System.err.println(settings);
        this.tasks = tasks;
        this.settings = settings;
    }

    public String createBongo(Bongo bongo) {
        if (bongo.running()) {
            bongo.stop();
        }
        
        Either<List<Task>, String> taskList = tasks.getBingoTasks();
        if (taskList.right().isPresent() || taskList.left().isEmpty()) {
            return taskList.right().isPresent() ? taskList.right().get() : "Unknown Error";
        }
        bongo.setSettings(settings, true);
        bongo.setTasks(taskList.left().get());
        return null;
    }
}
