package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import org.apache.commons.lang3.tuple.Triple;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

public class ClientAdvancementInfo {

    private static final Map<ResourceLocation, Triple<ItemStack, Component, Predicate<ItemStack>>> CACHE = new HashMap<>();

    public static ItemStack getDisplay(ResourceLocation id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id).getLeft();
        } else {
            return new ItemStack(Items.BARRIER);
        }
    }

    public static Component getTranslation(ResourceLocation id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id).getMiddle();
        } else {
            return new TranslatableComponent("bongo.task.advancement.invalid");
        }
    }

    public static Predicate<ItemStack> getTooltipItem(ResourceLocation id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id).getRight();
        } else {
            return stack -> false;
        }
    }

    public static void updateAdvancementInfo(ResourceLocation id, ItemStack display, Component translation, Predicate<ItemStack> tooltipItem) {
        CACHE.put(id, Triple.of(display, translation, tooltipItem));
    }
}
