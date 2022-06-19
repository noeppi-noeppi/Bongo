package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class PotionTextureRenderCache {

    private static final Map<ResourceLocation, ResourceLocation> CACHE = new HashMap<>();

    public static ResourceLocation getRenderTexture(MobEffect effect) {
        ResourceLocation id = Objects.requireNonNull(ForgeRegistries.MOB_EFFECTS.getKey(effect));
        if (CACHE.containsKey(id)) {
            return CACHE.get(id);
        } else {
            ResourceLocation texture = new ResourceLocation(id.getNamespace(), "textures/mob_effect/" + id.getPath() + ".png");
            CACHE.put(id, texture);
            return texture;
        }
    }
}
