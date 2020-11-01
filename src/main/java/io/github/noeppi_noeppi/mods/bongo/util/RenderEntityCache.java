package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.SnowGolemEntity;

import java.util.HashMap;
import java.util.Map;

public class RenderEntityCache {

    private static final Map<EntityType<?>, Entity> CACHE = new HashMap<>();

    public static <T extends Entity> T getRenderEntity(Minecraft mc, EntityType<T> type) {
        if (CACHE.containsKey(type)) {
            //noinspection unchecked
            return (T) CACHE.get(type);
        } else {
            @SuppressWarnings("ConstantConditions")
            T entity = type.create(mc.world);
            if (entity instanceof CatEntity) {
                // Make it black for undercover witches
                ((CatEntity) entity).setCatType(10);
            }
            // The game crashes if this is not there. But it does not crash in our render code
            // but when trying to render a chunk. Very weird.
            if (entity instanceof SnowGolemEntity)
                ((SnowGolemEntity) entity).setPumpkinEquipped(false);
            CACHE.put(type, entity);
            return entity;
        }
    }
}
