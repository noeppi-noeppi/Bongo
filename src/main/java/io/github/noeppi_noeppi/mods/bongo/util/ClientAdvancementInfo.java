package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.Map;

public class ClientAdvancementInfo {

    private static Map<ResourceLocation, Pair<ItemStack, ITextComponent>> CACHE = new HashMap<>();

    public static ItemStack getDisplay(ResourceLocation id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id).getLeft();
        } else {
            return new ItemStack(Items.BARRIER);
        }
    }

    public static ITextComponent getTranslation(ResourceLocation id) {
        if (CACHE.containsKey(id)) {
            return CACHE.get(id).getRight();
        } else {
            return new TranslationTextComponent("bongo.task.advancement.invalid");
        }
    }

    public static void updateAdvancementInfo(ResourceLocation id, ItemStack display, ITextComponent translation) {
        CACHE.put(id, Pair.of(display, translation));
    }
}
