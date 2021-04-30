package io.github.noeppi_noeppi.mods.bongo.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.libx.util.Misc;
import io.github.noeppi_noeppi.mods.bongo.util.TagWithCount;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.TagCollectionManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeTag implements TaskTypeSimple<TagWithCount> {

    public static final TaskTypeTag INSTANCE = new TaskTypeTag();

    private TaskTypeTag() {

    }

    @Override
    public Class<TagWithCount> getTaskClass() {
        return TagWithCount.class;
    }

    @Override
    public String getId() {
        return "bongo.tag";
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
    public void renderSlotContent(Minecraft mc, TagWithCount content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        ItemStack stack = cycle(content);
        RenderHelperItem.renderItemGui(matrixStack, buffer, stack == null ? new ItemStack(Items.BARRIER) : stack, 0, 0, 16, !bigBongo);
    }

    @Override
    public String getTranslatedContentName(TagWithCount content) {
        ItemStack stack = cycle(content);
        if (stack == null) {
            return I18n.format("bongo.task.tag.empty");
        } else {
            String text = stack.getDisplayName().getStringTruncated(16);
            if (content.getCount() > 1) {
                text += (" x " + content.getCount());
            }
            return text;
        }
    }

    @Override
    public ITextComponent getContentName(TagWithCount content, MinecraftServer server) {
        return new StringTextComponent(content.getId().toString());
    }

    @Override
    public boolean shouldComplete(TagWithCount element, PlayerEntity player, TagWithCount compare) {
        return element.getId().equals(compare.getId()) && element.getCount() <= compare.getCount();
    }

    @Override
    public void consumeItem(TagWithCount element, PlayerEntity player) {
        Util.removeItems(player, element.getCount(), stack -> element.getTag().contains(stack.getItem()));
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(TagWithCount element) {
        return stack -> element.getTag().contains(stack.getItem());
    }

    @Override
    public Set<ItemStack> bookmarkStacks(TagWithCount element) {
        //noinspection UnstableApiUsage
        return element.getTag().getAllElements().stream().map(ItemStack::new).collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public CompoundNBT serializeNBT(TagWithCount element) {
        return element.serialize();
    }

    @Override
    public TagWithCount deserializeNBT(CompoundNBT nbt) {
        return TagWithCount.deserialize(nbt);
    }

    @Override
    public void validate(TagWithCount element, MinecraftServer server) {
        if (!element.getId().equals(Misc.MISSIGNO) && element.getTag().getAllElements().isEmpty()) {
            throw new IllegalStateException("Empty or unknown tag: " + element.getId());
        }
    }

    @Override
    public TagWithCount copy(TagWithCount element) {
        return element.copy();
    }

    @Nullable
    @Override
    public Comparator<TagWithCount> getSorting() {
        return Comparator.comparing(TagWithCount::getId, Util.COMPARE_RESOURCE)
                .thenComparingInt(TagWithCount::getCount);
    }

    @Override
    public Stream<TagWithCount> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return TagCollectionManager.getManager().getItemTags().getRegisteredTags().stream()
                    .map(id -> new TagWithCount(id, 1));
        } else {
            return player.inventory.mainInventory.stream()
                    .filter(stack -> !stack.isEmpty())
                    .flatMap(stack -> TagCollectionManager.getManager().getItemTags().getOwningTags(stack.getItem()).stream()
                            .map(rl -> new TagWithCount(rl, stack.getCount()))
                    );
        }
    }

    @Nullable
    private static ItemStack cycle(TagWithCount element) {
        List<Item> items = element.getTag().getAllElements();
        if (items.isEmpty()) {
            return null;
        } else {
            ItemStack stack = new ItemStack(items.get((ClientTickHandler.ticksInGame / 20) % items.size()));
            stack.setCount(element.getCount());
            return stack;
        }
    }
}
