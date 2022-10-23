package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class ClientAdvancementInfo {

    private static final Map<ResourceLocation, Pair<ItemStack, Component>> CACHE = new HashMap<>();

    public static ItemStack getDisplay(ResourceLocation id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id).getLeft();
        } else {
            return new ItemStack(Items.BARRIER);
        }
    }

    public static Component getTranslation(ResourceLocation id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id).getRight();
        } else {
            return Component.translatable("bongo.task.advancement.invalid");
        }
    }

    public static void updateAdvancementInfo(ResourceLocation id, ItemStack display, Component translation) {
        CACHE.put(id, Pair.of(display, translation));
    }
}
