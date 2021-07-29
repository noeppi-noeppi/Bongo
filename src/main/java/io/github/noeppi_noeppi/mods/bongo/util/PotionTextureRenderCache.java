package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;

import java.util.HashMap;
import java.util.Map;

public class PotionTextureRenderCache {

    private static final Map<MobEffect, ResourceLocation> CACHE = new HashMap<>();

    public static ResourceLocation getRenderTexture(MobEffect effect) {
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
