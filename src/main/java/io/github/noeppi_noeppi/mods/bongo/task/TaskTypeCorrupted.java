package io.github.noeppi_noeppi.mods.bongo.task;

import com.dicemc.corruptedlands.ICorrupted;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
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

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeCorrupted implements TaskType<Block> {

    public static final TaskTypeCorrupted INSTANCE = new TaskTypeCorrupted();

    private TaskTypeCorrupted() {

    }

    @Override
    public Class<Block> getTaskClass() {
        return Block.class;
    }

    @Override
    public String getId() {
        return "spooky20.corrupted";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.corrupt";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(-1, -1, 0);
        AbstractGui.blit(matrixStack, 0, 0, 26, 38, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Block content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        RenderHelperItem.renderItemGui(matrixStack, buffer, new ItemStack(content), 0, 0, 16, false);
    }

    @Override
    public String getTranslatedContentName(Block content) {
        return I18n.format(content.getTranslationKey());
    }

    @Override
    public ITextComponent getContentName(Block content, MinecraftServer server) {
        return content.getTranslatedName();
    }

    @Override
    public boolean shouldComplete(Block element, PlayerEntity player, Block compare) {
        return element == compare;
    }

    @Override
    public CompoundNBT serializeNBT(Block element) {
        CompoundNBT nbt = new CompoundNBT();
        //noinspection ConstantConditions
        nbt.putString("block", element.getRegistryName().toString());
        return nbt;
    }

    @Override
    public Block deserializeNBT(CompoundNBT nbt) {
        return ForgeRegistries.BLOCKS.getValue(new ResourceLocation(nbt.getString("block")));
    }

    @Override
    public Stream<Block> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        return ForgeRegistries.BLOCKS.getValues().stream().filter(b -> b instanceof ICorrupted);
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(Block element) {
        return stack -> stack.getItem() == element.asItem();
    }
}
