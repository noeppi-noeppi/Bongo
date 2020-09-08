package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;

import java.util.HashMap;
import java.util.Map;

public class RenderEntityCache {

    private static Map<EntityType<?>, Entity> CACHE = new HashMap<>();

    public static <T extends Entity> T getRenderEntity(Minecraft mc, EntityType<T> type) {
        if (CACHE.containsKey(type)) {
            //noinspection unchecked
            return (T) CACHE.get(type);
        } else {
            @SuppressWarnings("ConstantConditions")
            T entity = type.create(mc.world);
            CACHE.put(type, entity);
            return entity;
        }
    }
}
