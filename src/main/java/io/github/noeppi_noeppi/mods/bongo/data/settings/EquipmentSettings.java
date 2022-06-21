package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.annotation.api.Codecs;
import org.moddingx.libx.annotation.codec.Param;
import org.moddingx.libx.annotation.codec.PrimaryConstructor;
import org.moddingx.libx.codec.MoreCodecs;

import java.util.List;

@PrimaryConstructor
public record EquipmentSettings(
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") List<ItemStack> inventory,
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") ItemStack head,
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") ItemStack chest,
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") ItemStack legs,
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") ItemStack feet,
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") List<ItemStack> backpack,
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") List<ItemStack> emergency
) {
    
    public static final Codec<EquipmentSettings> CODEC = Codecs.get(BongoMod.class, EquipmentSettings.class);

    public static final EquipmentSettings DEFAULT = new EquipmentSettings(
            List.of(),
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            ItemStack.EMPTY,
            List.of(),
            List.of()
    );
}
