package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.mods.bongo.data.GameDef;
import io.github.noeppi_noeppi.mods.bongo.network.BongoNetwork;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypeAdvancement;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypeItem;
import net.minecraft.client.resources.ReloadListener;
import net.minecraft.item.ItemStack;
import net.minecraft.profiler.IProfiler;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AdvancementEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class EventListener {

    @SubscribeEvent
    public void playerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        BongoNetwork.updateBongo(event.getPlayer());
    }

    @SubscribeEvent
    public void playerChangeDim(PlayerEvent.PlayerChangedDimensionEvent event) {
        BongoNetwork.updateBongo(event.getPlayer());
    }

    @SubscribeEvent
    public void advancementGrant(AdvancementEvent event) {
        World world = event.getPlayer().getEntityWorld();
        if (!world.isRemote) {
            Bongo.get(world).checkCompleted(TaskTypeAdvancement.INSTANCE, event.getPlayer(), event.getAdvancement().getId());
        }
    }

    @SubscribeEvent
    public void playerTick(TickEvent.PlayerTickEvent event) {
        if (!event.player.getEntityWorld().isRemote && event.player.ticksExisted % 20 == 0) {
            Bongo bongo = Bongo.get(event.player.world);
            for (final ItemStack stack : event.player.inventory.mainInventory) {
                if (!stack.isEmpty()) {
                    bongo.checkCompleted(TaskTypeItem.INSTANCE, event.player, stack);
                }
            }
        }
    }

    @SubscribeEvent
    public void resourcesReload(AddReloadListenerEvent event) {
        event.addListener(new ReloadListener<Object>() {
            @Nonnull
            @Override
            protected Object prepare(@Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
                return new Object();
            }

            @Override
            protected void apply(@Nonnull Object unused, @Nonnull IResourceManager resourceManager, @Nonnull IProfiler profiler) {
                try {
                    GameDef.loadGameDefs(resourceManager);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    @SubscribeEvent
    @OnlyIn(Dist.CLIENT)
    public void onItemTooltipEvent(ItemTooltipEvent event) {
        Bongo.addTooltip(event);
    }
}
