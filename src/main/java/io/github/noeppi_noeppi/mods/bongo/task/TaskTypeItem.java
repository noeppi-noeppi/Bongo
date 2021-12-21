package io.github.noeppi_noeppi.mods.bongo.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeItem implements TaskTypeSimple<ItemStack> {

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
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 18, 18, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, ItemStack content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ItemRenderUtil.renderItem(poseStack, buffer, content, !bigBongo);
    }

    @Override
    public String getTranslatedContentName(ItemStack content) {
        String text = content.getHoverName().getString(16);
        if (content.getCount() > 1) {
            text += (" x " + content.getCount());
        }
        return text;
    }

    @Override
    public Component getContentName(ItemStack content, MinecraftServer server) {
        return content.getHoverName();
    }

    @Override
    public boolean shouldComplete(ItemStack element, Player player, ItemStack compare) {
        if (ItemStack.isSameIgnoreDurability(element, compare) && element.getCount() <= compare.getCount()) {
            return Util.matchesNBT(element.getTag(), compare.getTag());
        } else {
            return false;
        }
    }

    @Override
    public void consumeItem(ItemStack element, Player player) {
        Util.removeItems(player, element.getCount(),
                stack -> ItemStack.isSameIgnoreDurability(element, stack)
                        && Util.matchesNBT(element.getTag(), stack.getTag()));
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(ItemStack element) {
        return stack -> ItemStack.isSame(element, stack) && Util.matchesNBT(element.getTag(), stack.getTag());
    }

    @Override
    public Set<ItemStack> bookmarkStacks(ItemStack element) {
        return ImmutableSet.of(element);
    }

    @Override
    public CompoundTag serializeNBT(ItemStack element) {
        return element.save(new CompoundTag());
    }

    @Override
    public ItemStack deserializeNBT(CompoundTag nbt) {
        if (!nbt.contains("Count")) {
            nbt.putByte("Count", (byte) 1);
        }
        ItemStack stack = ItemStack.of(nbt);
        if (stack.isEmpty()) {
            throw new IllegalStateException("Empty/Invalid item stack: " + (nbt.getString("id").isEmpty() ? nbt : nbt.getString("id")));
        }
        return stack;
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
    public Stream<ItemStack> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return ForgeRegistries.ITEMS.getValues().stream().flatMap(item -> {
                if (item.getItemCategory() != null) {
                    NonNullList<ItemStack> nl = NonNullList.create();
                    item.fillItemCategory(CreativeModeTab.TAB_SEARCH, nl);
                    return nl.stream();
                } else {
                    return Stream.of(new ItemStack(item));
                }
            }).filter(stack -> !stack.isEmpty());
        } else {
            return player.getInventory().items.stream().filter(stack -> !stack.isEmpty());
        } 
    }
}
