package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.item.Item;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tags.ITag;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;

import java.util.Objects;

public final class TagWithCount {
    
    private final ResourceLocation id;
    // Must be lazy as during deserialisation the tag may not have been read yet.
    private final LazyValue<ITag<Item>> tag;
    private final int count;

    public TagWithCount(ResourceLocation id, int count) {
        this.id = id;
        this.count = count;
        this.tag = new LazyValue<>(() -> {
            ITag<Item> tag = TagCollectionManager.getManager().getItemTags().get(id);
            if (tag == null) {
                return Tag.getEmptyTag();
            } else {
                return tag;
            }
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

    public ITag<Item> getTag() {
        return tag.getValue();
    }

    public int getCount() {
        return count;
    }
    
    public CompoundNBT serialize() {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("tag", id.toString());
        nbt.putInt("Count", count);
        return nbt;
    }
    
    public static TagWithCount deserialize(CompoundNBT nbt) {
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
