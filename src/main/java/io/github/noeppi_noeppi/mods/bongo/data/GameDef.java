package io.github.noeppi_noeppi.mods.bongo.data;

import com.mojang.datafixers.util.Either;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.data.settings.GameSettings;
import io.github.noeppi_noeppi.mods.bongo.data.task.GameTasks;
import io.github.noeppi_noeppi.mods.bongo.task.Task;
import org.moddingx.libx.util.lazy.LazyValue;

import java.util.List;
import java.util.function.Supplier;

public class GameDef {

    public final LazyValue<GameTasks> tasks;
    public final LazyValue<GameSettings> settings;

    public GameDef(Supplier<GameTasks> tasks, Supplier<GameSettings> settings) {
        this.tasks = new LazyValue<>(tasks);
        this.settings = new LazyValue<>(settings);
    }

    public String createBongo(Bongo bongo) {
        if (bongo.running()) {
            bongo.stop();
        }
        
        GameTasks tasks;
        GameSettings settings;
        try {
            tasks = this.tasks.get();
            settings = this.settings.get();
        } catch (RuntimeException e) {
            return e.getMessage();
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
