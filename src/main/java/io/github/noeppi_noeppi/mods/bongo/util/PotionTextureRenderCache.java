package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.potion.Effect;
import net.minecraft.util.ResourceLocation;

import java.util.HashMap;
import java.util.Map;

public class PotionTextureRenderCache {

    private static final Map<Effect, ResourceLocation> CACHE = new HashMap<>();

    public static ResourceLocation getRenderTexture(Effect effect) {
        if (CACHE.containsKey(effect)) {
            return CACHE.get(effect);
        } else {
            @SuppressWarnings("ConstantConditions")
            ResourceLocation texture = new ResourceLocation(effect.getRegistryName().getNamespace(), "textures/mob_effect/" + effect.getRegistryName().getPath() + ".png");
            CACHE.put(effect, texture);
            return texture;
        }
    }
}
