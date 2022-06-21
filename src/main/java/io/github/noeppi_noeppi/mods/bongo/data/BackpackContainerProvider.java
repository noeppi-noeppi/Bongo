package io.github.noeppi_noeppi.mods.bongo.data;

import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ChestMenu;
import net.minecraftforge.items.ItemStackHandler;
import org.moddingx.libx.inventory.VanillaWrapper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BackpackContainerProvider implements MenuProvider {

    private final Component team;
    private final ItemStackHandler handler;
    private final Runnable dirty;

    public BackpackContainerProvider(Component team, ItemStackHandler handler, Runnable dirty) {
        this.team = team;
        this.handler = handler;
        this.dirty = dirty;
    }

    @Nonnull
    @Override
    public Component getDisplayName() {
        return Component.translatable("bongo.backpack").append(team);
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, @Nonnull Inventory inventory, @Nonnull Player player) {
        return ChestMenu.threeRows(containerId, inventory, new VanillaWrapper(handler, dirty));
    }
}
