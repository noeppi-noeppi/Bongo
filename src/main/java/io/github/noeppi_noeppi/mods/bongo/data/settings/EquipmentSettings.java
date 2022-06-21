package io.github.noeppi_noeppi.mods.bongo.data.settings;

import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.data.Team;
import net.minecraft.server.level.ServerPlayer;
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
        @Param(value = MoreCodecs.class, field = "SAFE_ITEM_STACK") ItemStack offhand,
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
            ItemStack.EMPTY,
            List.of(),
            List.of()
    );
    
    public void equip(Team team, boolean suppressBingoSync) {
        team.clearBackPack(true);
        for (int slot = 0 ; slot < Math.min(backpack().size(), 27); slot++) {
            team.getBackPack().setStackInSlot(slot, backpack().get(slot).copy());
        }
        team.setChanged(suppressBingoSync);
    }
    
    public void equip(ServerPlayer player) {
        player.getInventory().clearContent();
        for (int slot = 0 ; slot < Math.min(inventory().size(), player.getInventory().items.size()); slot++) {
            player.getInventory().items.set(slot, inventory().get(slot).copy());
        }
        player.getInventory().offhand.set(0, offhand().copy());
        player.getInventory().armor.set(0, feet().copy());
        player.getInventory().armor.set(1, legs().copy());
        player.getInventory().armor.set(2, chest().copy());
        player.getInventory().armor.set(3, head().copy());
    }
    
    public boolean hasEmergencyItems() {
        return !this.emergency().isEmpty();
    }
    
    public void giveEmergencyItems(ServerPlayer player) {
        for (ItemStack stack : this.emergency()) {
            if (!player.getInventory().add(stack.copy())) {
                player.drop(stack.copy(), false);
            }
        }
    }
}
