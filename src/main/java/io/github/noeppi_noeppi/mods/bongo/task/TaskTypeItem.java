package io.github.noeppi_noeppi.mods.bongo.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.NonNullList;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeItem implements TaskType<ItemStack> {

    public static final TaskTypeItem INSTANCE = new TaskTypeItem();

    private TaskTypeItem() {

    }

    @Override
    public Class<ItemStack> getTaskClass() {
        return ItemStack.class;
    }

    @Override
    public String getId() {
        return "bongo.item";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.task.item.name";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 18, 18, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, ItemStack content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        RenderHelperItem.renderItemGui(matrixStack, buffer, content, 0, 0, 16, !bigBongo);
    }

    @Override
    public String getTranslatedContentName(ItemStack content) {
        String text = content.getDisplayName().getStringTruncated(16);
        
        if (content.getCount() > 1)
            text += (" x " + content.getCount());

        return text;
    }

    @Override
    public ITextComponent getContentName(ItemStack content, MinecraftServer server) {
        return content.getDisplayName();
    }

    @Override
    public boolean shouldComplete(ItemStack element, PlayerEntity player, ItemStack compare) {
        if (ItemStack.areItemsEqualIgnoreDurability(element, compare) && element.getCount() <= compare.getCount()) {
            return Util.matchesNBT(element.getTag(), compare.getTag());
        } else {
            return false;
        }
    }

    @Override
    public void consumeItem(ItemStack element, PlayerEntity player) {
        int removeLeft = element.getCount();
        for (int slot = 0; slot < player.inventory.getSizeInventory(); slot++) {
            if (removeLeft <= 0) {
                break;
            }
            ItemStack playerSlot = player.inventory.getStackInSlot(slot);
            if (ItemStack.areItemsEqualIgnoreDurability(element, playerSlot)) {
                if (Util.matchesNBT(element.getTag(), playerSlot.getTag())) {
                    int rem = Math.min(removeLeft, playerSlot.getCount());
                    playerSlot.shrink(rem);
                    player.inventory.setInventorySlotContents(slot, playerSlot.isEmpty() ? ItemStack.EMPTY : playerSlot);
                    removeLeft -= rem;
                }
            }
        }
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(ItemStack element) {
        return stack -> ItemStack.areItemsEqual(element, stack) && Util.matchesNBT(element.getTag(), stack.getTag());
    }

    @Override
    public Set<ItemStack> bookmarkStacks(ItemStack element) {
        return ImmutableSet.of(element);
    }

    @Override
    public CompoundNBT serializeNBT(ItemStack element) {
        return element.write(new CompoundNBT());
    }

    @Override
    public ItemStack deserializeNBT(CompoundNBT nbt) {
        if (!nbt.contains("Count")) {
            nbt.putByte("Count", (byte) 1);
        }
        return ItemStack.read(nbt);
    }

    @Override
    public ItemStack copy(ItemStack element) {
        return element.copy();
    }

    @Nullable
    @Override
    public Comparator<ItemStack> getSorting() {
        return Comparator.comparing((ItemStack stack) -> stack.getItem().getRegistryName(), Util.COMPARE_RESOURCE)
                .thenComparingInt(ItemStack::getCount);
    }
    
    @Override
    public Stream<ItemStack> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return ForgeRegistries.ITEMS.getValues().stream().flatMap(item -> {
                if (item.getGroup() != null) {
                    NonNullList<ItemStack> nl = NonNullList.create();
                    item.fillItemGroup(item.getGroup(), nl);
                    return nl.stream();
                } else {
                    return Stream.of(new ItemStack(item));
                }
            }).filter(stack -> !stack.isEmpty());
        } else {
            return player.inventory.mainInventory.stream().filter(stack -> !stack.isEmpty());
        }
    }
}
