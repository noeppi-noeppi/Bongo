package io.github.noeppi_noeppi.mods.bongo.util;

import io.github.noeppi_noeppi.libx.util.LazyValue;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.SetTag;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;

import java.util.Objects;

public final class TagWithCount {
    
    private final ResourceLocation id;
    // Must be lazy as during deserialisation the tag may not have been read yet.
    private final LazyValue<Tag<Item>> tag;
    private final int count;

    public TagWithCount(ResourceLocation id, int count) {
        this.id = id;
        this.count = count;
        this.tag = new LazyValue<>(() -> {
            Tag<Item> tag = ItemTags.getAllTags().getTag(id);
            return tag == null ? SetTag.empty() : tag;
        });
    }
    
    private TagWithCount(TagWithCount parent, int count) {
        // We leave the lazy value here
        this.id = parent.id;
        this.count = count;
        this.tag = parent.tag;
    }

    public ResourceLocation getId() {
        return id;
    }

    public Tag<Item> getTag() {
        return tag.get();
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
