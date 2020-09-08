package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;

public class Task implements INBTSerializable<CompoundNBT> {

    public static Task empty() {
        return new Task(TaskTypeEmpty.INSTANCE, TaskTypeEmpty.INSTANCE);
    }

    private TaskType<?> type;
    private Object element;

    public <T> Task(TaskType<T> type, T element) {
        this.type = type;
        this.element = element;
    }

    public TaskType<?> getType() {
        return type;
    }

    public String getTranslatedName() {
        return type.getTranslatedName();
    }

    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        type.renderSlot(mc, matrixStack, buffer);
    }

    public void renderSlotContent(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        //noinspection unchecked
        ((TaskType<Object>) type).renderSlotContent(mc, element, matrixStack, buffer);
    }

    public String getTranslatedContentName() {
        //noinspection unchecked
        return ((TaskType<Object>) type).getTranslatedContentName(element);
    }

    public ITextComponent getContentName(MinecraftServer server) {
        //noinspection unchecked
        return ((TaskType<Object>) type).getContentName(element, server);
    }

    public void syncToClient(MinecraftServer server, @Nullable ServerPlayerEntity syncTarget) {
        //noinspection unchecked
        ((TaskType<Object>) type).syncToClient(element, server, syncTarget);
    }

    public boolean shouldComplete(PlayerEntity player, Object compare) {
        if (!type.getTaskClass().isAssignableFrom(compare.getClass())) {
            return false;
        }
        //noinspection unchecked
        return ((TaskType<Object>) type).shouldComplete(element, player, compare);
    }

    public ItemStack bongoTooltipStack() {
        //noinspection unchecked
        return ((TaskType<Object>) type).bongoTooltipStack(element);
    }

    @Override
    public CompoundNBT serializeNBT() {
        @SuppressWarnings("unchecked")
        CompoundNBT nbt = ((TaskType<Object>) type).serializeNBT(element);
        nbt.putString("type", type.getId());
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt) {
        type = TaskTypes.getType(nbt.getString("type"));
        if (type == null) {
            throw new IllegalStateException("Could not deserialse Bongo Task: Unknown Task Type: " + nbt.getString("type"));
        }
        element = type.deserializeNBT(nbt);
    }

    public Task copy() {
        //noinspection unchecked
        return new Task((TaskType<Object>) type, ((TaskType<Object>) type).copy(element));
    }
}
