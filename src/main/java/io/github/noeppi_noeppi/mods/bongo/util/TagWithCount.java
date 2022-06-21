package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;
import org.moddingx.libx.util.data.TagAccess;
import org.moddingx.libx.util.lazy.CachedValue;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class TagWithCount {
    
    public static final Codec<TagWithCount> CODEC = Codecs.get(BongoMod.class, TagWithCount.class);
    
    private final ResourceLocation id;
    private final TagKey<Item> tag;
    private final CachedValue<Predicate<Item>> contains;
    private final CachedValue<List<Item>> itemList;
    private final int count;

    @PrimaryConstructor
    public TagWithCount(ResourceLocation id, int count) {
        this.id = id;
        this.count = count;
        this.tag = TagKey.create(Registry.ITEM_REGISTRY, id);
        this.contains = new CachedValue<>(() -> {
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
        this.itemList = new CachedValue<>(() -> {
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

    // Will reset the lazy value
    public void invalidate() {
        this.contains.invalidate();
        this.itemList.invalidate();
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
