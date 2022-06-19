package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.function.Predicate;

public abstract sealed class Highlight<T> {
    
    protected final T element;

    public Highlight(T element) {
        this.element = element;
    }

    public T element() {
        return this.element;
    }

    public static final class Item extends Highlight<ItemStack> {
        
        private final Predicate<ItemStack> predicate;
        
        public Item(ItemStack element) {
            super(element.copy());
            this.predicate = stack -> stack.getItem() == this.element.getItem();
        }

        public Predicate<ItemStack> predicate() {
            return this.predicate;
        }
    }
    
    public static final class Advancement extends Highlight<ResourceLocation> {
        
        public Advancement(ResourceLocation element) {
            super(element);
        }
    }
}
