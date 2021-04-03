package io.github.noeppi_noeppi.mods.bongo.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockEgg;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeEgg implements TaskType<BlockEgg<?>> {

    public static final TaskTypeEgg INSTANCE = new TaskTypeEgg();

    private TaskTypeEgg() {

    }
    
    @Override
    public Class<BlockEgg<?>> getTaskClass() {
        //noinspection unchecked
        return (Class<BlockEgg<?>>) (Class<?>) BlockEgg.class;
    }

    @Override
    public String getId() {
        return "bongo.easter";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.easter";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        
    }

    @Override
    public void renderSlotContent(Minecraft mc, BlockEgg<?> content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        RenderHelperItem.renderItemGui(matrixStack, buffer, new ItemStack(content), -2, -2, 20, false);
    }

    @Override
    public String getTranslatedContentName(BlockEgg<?> content) {
        return I18n.format("bongo.easter");
    }

    @Override
    public ITextComponent getContentName(BlockEgg<?> content, MinecraftServer server) {
        return new ItemStack(content).getDisplayName();
    }

    @Override
    public boolean shouldComplete(BlockEgg<?> element, PlayerEntity player, BlockEgg<?> compare) {
        return element == compare;
    }

    @Override
    public CompoundNBT serializeNBT(BlockEgg<?> element) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("id", Objects.requireNonNull(element.getRegistryName()).toString());
        return nbt;
    }

    @Override
    public BlockEgg<?> deserializeNBT(CompoundNBT nbt) {
        ResourceLocation rl = new ResourceLocation(nbt.getString("id"));
        Block block = ForgeRegistries.BLOCKS.getValue(rl);
        if (block instanceof BlockEgg<?>) {
            return (BlockEgg<?>) block;
        } else {
            return null;
        }
    }

    @Override
    public Stream<BlockEgg<?>> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        return ForgeRegistries.BLOCKS.getValues().stream()
                .filter(block -> block instanceof BlockEgg<?>)
                .map(block -> (BlockEgg<?>) block);
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(BlockEgg<?> element) {
        return stack -> stack.getItem() == element.asItem();
    }

    @Override
    public Set<ItemStack> bookmarkStacks(BlockEgg<?> element) {
        return ImmutableSet.of(new ItemStack(element));
    }

    @Nullable
    @Override
    public Comparator<BlockEgg<?>> getSorting() {
        return Comparator.comparing(ForgeRegistryEntry::getRegistryName, Util.COMPARE_RESOURCE);
    }
}
