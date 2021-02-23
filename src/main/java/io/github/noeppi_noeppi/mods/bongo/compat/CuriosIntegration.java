package io.github.noeppi_noeppi.mods.bongo.compat;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosIntegration {
    public static void clearTrinkets(PlayerEntity player) {
        CuriosApi.getCuriosHelper().getCuriosHandler(player).ifPresent((handler) -> {
            handler.getCurios().forEach((id, type) -> {
                for (int i = 0; i < type.getSlots(); i++) {
                    type.getStacks().setStackInSlot(i, ItemStack.EMPTY);
                    type.getCosmeticStacks().setStackInSlot(i, ItemStack.EMPTY);
                }
            });
        });
    }
}
