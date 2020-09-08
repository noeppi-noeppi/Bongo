package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public interface TaskType<T> {

    Class<T> getTaskClass();

    String getId();

    default String getTranslatedName() {
        return I18n.format(getTranslationKey());
    }

    String getTranslationKey();

    void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer);

    void renderSlotContent(Minecraft mc, T content, MatrixStack matrixStack, IRenderTypeBuffer buffer);

    String getTranslatedContentName(T content);

    ITextComponent getContentName(T content, MinecraftServer server);

    boolean shouldComplete(T element, PlayerEntity player, T compare);

    CompoundNBT serializeNBT(T element);

    T deserializeNBT(CompoundNBT nbt);

    default T copy(T element) {
        return element;
    }

    default void syncToClient(T element, MinecraftServer server, @Nullable ServerPlayerEntity syncTarget) {

    }

    default ItemStack bongoTooltipStack(T element) {
        return ItemStack.EMPTY;
    }
}
