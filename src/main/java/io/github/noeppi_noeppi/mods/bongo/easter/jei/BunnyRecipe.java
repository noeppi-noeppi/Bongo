package io.github.noeppi_noeppi.mods.bongo.easter.jei;

import net.minecraft.item.ItemStack;

public class BunnyRecipe {
    
    private final ItemStack input;
    private final ItemStack output;

    public BunnyRecipe(ItemStack input, ItemStack output) {
        this.input = input;
        this.output = output;
    }

    public ItemStack getInput() {
        return input;
    }

    public ItemStack getOutput() {
        return output;
    }
}
