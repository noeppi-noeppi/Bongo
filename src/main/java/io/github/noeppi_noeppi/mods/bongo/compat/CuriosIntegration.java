package io.github.noeppi_noeppi.mods.bongo.compat;

import io.github.noeppi_noeppi.mods.bongo.event.BongoStartEvent;
import net.minecraft.item.ItemStack;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import top.theillusivec4.curios.api.CuriosApi;

public class CuriosIntegration {
    
    @SubscribeEvent
    public void clearTrinkets(BongoStartEvent.Player event) {
        //noinspection CodeBlock2Expr
        CuriosApi.getCuriosHelper().getCuriosHandler(event.getPlayer()).ifPresent((handler) -> {
            handler.getCurios().forEach((id, type) -> {
                for (int i = 0; i < type.getSlots(); i++) {
                    type.getStacks().setStackInSlot(i, ItemStack.EMPTY);
                    type.getCosmeticStacks().setStackInSlot(i, ItemStack.EMPTY);
                }
            });
        });
    }
}
