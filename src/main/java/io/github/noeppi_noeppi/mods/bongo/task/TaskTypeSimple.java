package io.github.noeppi_noeppi.mods.bongo.task;

import net.minecraft.entity.player.PlayerEntity;

public interface TaskTypeSimple<T> extends TaskType<T, T> {

    default void consumeItem(T element, PlayerEntity player) {
        
    }
    
    @Override
    default Class<T> getCompareClass() {
        return getTaskClass();
    }

    @Deprecated
    @Override
    default void consumeItem(T element, T found, PlayerEntity player) {
        consumeItem(element, player);
    }
}
