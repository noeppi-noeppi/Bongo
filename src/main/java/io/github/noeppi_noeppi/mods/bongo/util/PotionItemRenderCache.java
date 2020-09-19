package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;

import java.util.HashMap;
import java.util.Map;

public class PotionItemRenderCache {

    private static final Map<Effect, ItemStack> CACHE = new HashMap<>();

    public static ItemStack getRenderStack(Effect effect) {
        if (CACHE.containsKey(effect)) {
            return CACHE.get(effect);
        } else {
            ItemStack stack = new ItemStack(Items.POTION);
            CompoundNBT nbt = stack.getOrCreateTag();
            nbt.putInt("CustomPotionColor", effect.getLiquidColor());
            CACHE.put(effect, stack);
            return stack;
        }
    }
}
