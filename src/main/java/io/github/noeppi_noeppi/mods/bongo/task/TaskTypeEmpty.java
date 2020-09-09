package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public class TaskTypeEmpty implements TaskType<TaskTypeEmpty> {

    public static final TaskTypeEmpty INSTANCE = new TaskTypeEmpty();

    private TaskTypeEmpty() {

    }

    @Override
    public Class<TaskTypeEmpty> getTaskClass() {
        return TaskTypeEmpty.class;
    }

    @Override
    public String getId() {
        return "bongo.empty";
    }

    @Override
    public String getTranslatedName() {
        return "";
    }

    @Override
    public String getTranslationKey() {
        return "";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {

    }

    @Override
    public void renderSlotContent(Minecraft mc, TaskTypeEmpty content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {

    }

    @Override
    public String getTranslatedContentName(TaskTypeEmpty content) {
        return "";
    }

    @Override
    public ITextComponent getContentName(TaskTypeEmpty content, MinecraftServer server) {
        return new StringTextComponent("");
    }

    @Override
    public boolean shouldComplete(TaskTypeEmpty element, PlayerEntity player, TaskTypeEmpty compare) {
        return false;
    }

    @Override
    public CompoundNBT serializeNBT(TaskTypeEmpty element) {
        return new CompoundNBT();
    }

    @Override
    public TaskTypeEmpty deserializeNBT(CompoundNBT nbt) {
        return this;
    }
}
