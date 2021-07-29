package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.animal.SnowGolem;

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
            T entity = type.create(mc.level);
            if (entity instanceof Mob) {
                ((Mob) entity).setNoAi(true);
            }
            // The game crashes if this is not there. But it does not crash in our render code
            // but when trying to render a chunk. Very weird.
            if (entity instanceof SnowGolem) {
                ((SnowGolem) entity).setPumpkin(false);
            }
            CACHE.put(type, entity);
            return entity;
        }
    }
}
