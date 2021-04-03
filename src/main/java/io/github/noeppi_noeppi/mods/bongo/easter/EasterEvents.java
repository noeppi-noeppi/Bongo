package io.github.noeppi_noeppi.mods.bongo.easter;

import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class EasterEvents {

    @SubscribeEvent
    public void playerRightClickEntity(PlayerInteractEvent.EntityInteract event) {
        if (!event.getWorld().isRemote && event.getTarget() instanceof RabbitEntity) {
            ItemStack held = event.getItemStack();
            if (EggHandler.handleBunnyFood((RabbitEntity) event.getTarget(), held)) {
                held.shrink(1);
                event.getPlayer().setHeldItem(event.getHand(), held);
                event.setCanceled(true);
            }
        }
    }
}
