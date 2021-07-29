package io.github.noeppi_noeppi.mods.bongo.task;

import net.minecraft.world.entity.player.Player;

public interface TaskTypeSimple<T> extends TaskType<T, T> {

    default void consumeItem(T element, Player player) {
        
    }
    
    @Override
    default Class<T> getCompareClass() {
        return getTaskClass();
    }

    @Deprecated
    @Override
    default void consumeItem(T element, T found, Player player) {
        consumeItem(element, player);
    }
}
