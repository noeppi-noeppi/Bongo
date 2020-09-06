package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;

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

    boolean shouldComplete(T element, PlayerEntity player, T compare);

    CompoundNBT serializeNBT(T element);

    T deserializeNBT(CompoundNBT nbt);

    default T copy(T element) {
        return element;
    }
}
