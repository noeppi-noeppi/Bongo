package io.github.noeppi_noeppi.mods.bongo.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.crafting.CraftingHelper;
import org.moddingx.libx.codec.CodecHelper;
import org.moddingx.libx.codec.MoreCodecs;
import org.moddingx.libx.codec.TypedEncoder;
import org.moddingx.libx.crafting.RecipeHelper;

import java.util.Objects;
import java.util.Set;

// When an ItemStack is loaded, it automatically fixes its nbt tag
// to for example contain a Damage key.
// This class provides ways to read the stack without this happening
public class StackTagReader {
    
    public static final Codec<ItemStack> DIRECT_STACK_CODEC = MoreCodecs.typeMapped(
            Codec.STRING.flatXmap(
                    str -> CodecHelper.doesNotThrow(() -> fromNBT(TagParser.parseTag(str))),
                    stack -> CodecHelper.doesNotThrow(() -> stack.save(new CompoundTag()).toString())
            ),
            TypedEncoder.of(Tag.class, stack -> stack.save(new CompoundTag()), tag -> fromNBT((CompoundTag) tag)),
            TypedEncoder.of(JsonElement.class, stack -> RecipeHelper.serializeItemStack(stack, true), json -> fromJson(json.getAsJsonObject()))
    );
    
    public static ItemStack fromNBT(CompoundTag nbt) {
        CompoundTag theTag = new CompoundTag();
        if (nbt.contains("tag", Tag.TAG_COMPOUND)) {
            theTag = nbt.getCompound("tag").copy();
        }
        ItemStack stack = ItemStack.of(nbt);
        if (stack.isEmpty() && !stack.hasTag()) return stack;
        copyInto(theTag, stack.getOrCreateTag());
        return stack;
    }
    
    public static ItemStack fromJson(JsonObject json) {
        CompoundTag theTag = new CompoundTag();
        if (json.has("nbt")) {
            theTag = CraftingHelper.getNBT(json.get("nbt"));
            theTag.remove("ForgeCaps");
        }
        ItemStack stack = CraftingHelper.getItemStack(json, true);
        if (stack.isEmpty() && !stack.hasTag()) return stack;
        copyInto(theTag, stack.getOrCreateTag());
        return stack;
    }
    
    private static void copyInto(CompoundTag from, CompoundTag to) {
        Set<String> allKeys = Set.copyOf(to.getAllKeys());
        allKeys.forEach(to::remove);
        for (String key : from.getAllKeys()) {
            to.put(key, Objects.requireNonNull(from.get(key)).copy());
        }
    }
}
