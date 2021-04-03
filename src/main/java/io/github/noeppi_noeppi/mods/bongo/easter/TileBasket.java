package io.github.noeppi_noeppi.mods.bongo.easter;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandlerModifiable;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TileBasket extends TileEntityBase /*implements IItemHandlerModifiable*/ {

    private final List<ItemStack> contents = new ArrayList<>();

    public TileBasket(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn/*, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY*/);
    }

    public List<ItemStack> getRenderView() {
        return Collections.unmodifiableList(contents.subList(Math.max(0, contents.size() - 16), contents.size()));
    }

    @Override
    public void read(@Nonnull BlockState state, @Nonnull CompoundNBT nbt) {
        super.read(state, nbt);
        contents.clear();
        if (nbt.contains("items", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) contents.add(ItemStack.read(list.getCompound(i)));
            removeEmpties();
        }
    }

    @Nonnull
    @Override
    public CompoundNBT write(@Nonnull CompoundNBT nbt) {
        ListNBT list = new ListNBT();
        contents.forEach(stack -> list.add(stack.write(new CompoundNBT())));
        nbt.put("items", list);
        return super.write(nbt);
    }

    @Override
    public void handleUpdateTag(BlockState state, CompoundNBT nbt) {
        super.handleUpdateTag(state, nbt);
        contents.clear();
        if (nbt.contains("items", Constants.NBT.TAG_LIST)) {
            ListNBT list = nbt.getList("items", Constants.NBT.TAG_COMPOUND);
            for (int i = 0; i < list.size(); i++) contents.add(ItemStack.read(list.getCompound(i)));
            removeEmpties();
        }
    }
    
    @Nonnull
    @Override
    public CompoundNBT getUpdateTag() {
        CompoundNBT nbt = super.getUpdateTag();
        ListNBT list = new ListNBT();
        contents.forEach(stack -> list.add(stack.write(new CompoundNBT())));
        nbt.put("items", list);
        return super.write(nbt);
    }

    /*@Override
    public void setStackInSlot(int slot, @Nonnull ItemStack stack) {
        if (slot < contents.size()) {
            contents.set(slot, stack);
        } else if (slot < contents.size() + 1) {
            contents.add(stack);
        }
        removeEmpties();
        markDirty();
        markDispatchable();
    }

    @Override
    public int getSlots() {
        return contents.size() + 1;
    }

    @Nonnull
    @Override
    public ItemStack getStackInSlot(int slot) {
        if (slot < contents.size()) {
            return contents.get(slot);
        } else {
            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate) {
        if (slot < contents.size() || !isItemValid(slot, stack) || stack.isEmpty()) {
            return stack;
        } else {
            if (!simulate) {
                contents.add(stack);
                markDirty();
                markDispatchable();
            }
            return ItemStack.EMPTY;
        }
    }

    @Nonnull
    @Override
    public ItemStack extractItem(int slot, int amount, boolean simulate) {
        if (slot >= contents.size()) {
            return ItemStack.EMPTY;
        } else {
            ItemStack stack;
            if (simulate) {
                stack = contents.get(slot).copy();
            } else {
                stack = contents.remove(slot);
                markDirty();
                markDispatchable();
            }
            return stack;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack) {
        return stack.getItem() instanceof BlockItem && ((BlockItem) stack.getItem()).getBlock() instanceof BlockEgg;
    }*/

    private void removeEmpties() {
        contents.removeIf(ItemStack::isEmpty);
        this.markDirty();
        this.markDispatchable();
    }
}
