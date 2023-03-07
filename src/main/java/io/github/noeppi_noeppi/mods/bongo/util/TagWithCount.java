package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITag;
import net.minecraftforge.registries.tags.ITagManager;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;
import org.moddingx.libx.util.lazy.CachedValue;

import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public final class TagWithCount {
    
    public static final Codec<TagWithCount> CODEC = Codecs.get(BongoMod.class, TagWithCount.class);
    
    private final ResourceLocation id;
    private final TagKey<Item> key;
    private final CachedValue<Predicate<Item>> contains;
    private final CachedValue<List<Item>> itemList;
    private final int count;

    @PrimaryConstructor
    public TagWithCount(ResourceLocation id, int count) {
        this.id = id;
        this.count = count;
        this.key = TagKey.create(Registries.ITEM, id);
        this.contains = new CachedValue<>(() -> {
            try {
                ITagManager<Item> mgr = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
                if (!mgr.isKnownTagName(this.key)) return item -> false;
                ITag<Item> theTag = mgr.getTag(this.key);
                return theTag::contains;
            } catch (Exception e) {
                e.printStackTrace();
                return item -> false;
            }
        });
        this.itemList = new CachedValue<>(() -> {
            try {
                ITagManager<Item> mgr = Objects.requireNonNull(ForgeRegistries.ITEMS.tags());
                if (!mgr.isKnownTagName(this.key)) return List.of();
                return mgr.getTag(this.key).stream().toList();
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
        this.key = parent.key;
        this.contains = parent.contains.copy();
        this.itemList = parent.itemList.copy();
    }

    public ResourceLocation getId() {
        return id;
    }

    public TagKey<Item> getKey() {
        return key;
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
