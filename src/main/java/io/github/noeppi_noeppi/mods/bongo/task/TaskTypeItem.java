package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.mods.bongo.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

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
    public void renderSlotContent(Minecraft mc, ItemStack content, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        RenderHelper.renderItemGui(matrixStack, buffer, content, 0, 0, 16);
    }

    @Override
    public String getTranslatedContentName(ItemStack content) {
        String text = content.getTextComponent().getStringTruncated(16);
        if (text.startsWith("["))
            text = text.substring(1);
        if (text.endsWith("]"))
            text = text.substring(0, text.length() - 1);
        return text;
    }

    @Override
    public ITextComponent getContentName(ItemStack content, MinecraftServer server) {
        return content.getTextComponent();
    }

    @Override
    public boolean shouldComplete(ItemStack element, PlayerEntity player, ItemStack compare) {
        return ItemStack.areItemsEqualIgnoreDurability(element, compare); // TODO check for nbt
    }

    @Override
    public ItemStack bongoTooltipStack(ItemStack element) {
        return element;
    }

    @Override
    public CompoundNBT serializeNBT(ItemStack element) {
        return element.write(new CompoundNBT());
    }

    @Override
    public ItemStack deserializeNBT(CompoundNBT nbt) {
        return ItemStack.read(nbt);
    }

    @Override
    public ItemStack copy(ItemStack element) {
        return element.copy();
    }
}
