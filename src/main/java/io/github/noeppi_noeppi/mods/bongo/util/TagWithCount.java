package io.github.noeppi_noeppi.mods.bongo.util;

import io.github.noeppi_noeppi.libx.util.LazyValue;
import io.github.noeppi_noeppi.libx.util.TagAccess;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class TagWithCount {
    
    private final ResourceLocation id;
    private final TagKey<Item> tag;
    private final LazyValue<Predicate<Item>> contains;
    private final LazyValue<List<Item>> itemList;
    private final int count;

    public TagWithCount(ResourceLocation id, int count) {
        this.id = id;
        this.count = count;
        this.tag = TagKey.create(Registry.ITEM_REGISTRY, id);
        this.contains = new LazyValue<>(() -> {
            try {
                // Can't use TagAccess for performance reasons
                @SuppressWarnings("unchecked")
                Registry<Item> registry = (Registry<Item>) Registry.REGISTRY.get(Registry.ITEM_REGISTRY.location());
                if (registry == null) throw new IllegalStateException("Item registry not found");
                HolderSet.Named<Item> tag = TagAccess.ROOT.get(this.tag);
                return item -> registry.getHolder(registry.getId(item)).map(tag::contains).orElse(false);
            } catch (Exception e) {
                e.printStackTrace();
                return item -> false;
            }
        });
        this.itemList = new LazyValue<>(() -> {
            try {
                HolderSet.Named<Item> tag = TagAccess.ROOT.get(this.tag);
                return tag.stream().map(Holder::value).toList();
            } catch (Exception e) {
                e.printStackTrace();
                return List.of();
            }
        });
    }
    
    private TagWithCount(TagWithCount parent, int count) {
        // We leave the lazy value here
        this.id = parent.id;
        this.count = count;
        this.tag = parent.tag;
        // Keep the lazy value for performance reasons
        this.contains = parent.contains;
        this.itemList = parent.itemList;
    }

    public ResourceLocation getId() {
        return id;
    }

    public TagKey<Item> getTag() {
        return tag;
    }

    public List<Item> getItems() {
        return itemList.get();
    }
    
    public boolean contains(Item item) {
        return contains.get().test(item);
    }

    public int getCount() {
        return count;
    }
    
    public CompoundTag serialize() {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("tag", id.toString());
        nbt.putInt("Count", count);
        return nbt;
    }
    
    public static TagWithCount deserialize(CompoundTag nbt) {
        ResourceLocation id = Util.getLocationFor(nbt, "tag");
        int count = nbt.contains("Count") ? nbt.getInt("Count") : 1;
        if (count <= 0) {
            throw new IllegalStateException("Tasks with no items are not allowed: Invalid count: " + count);
        }
        return new TagWithCount(id, count);
    }

    // Will reset the lazy value
    public TagWithCount copy() {
        return new TagWithCount(id, count);
    }
    
    public TagWithCount withCount(int count) {
        return new TagWithCount(this, count);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TagWithCount that = (TagWithCount) o;
        return count == that.count && id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TagWithCount[ " + id + " x " + count + " ]";
    }
}
