package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TaskTypeAlways implements TaskType<TaskTypeAlways> {

    public static final TaskTypeAlways INSTANCE = new TaskTypeAlways();

    private TaskTypeAlways() {

    }

    @Override
    public Class<TaskTypeAlways> getTaskClass() {
        return TaskTypeAlways.class;
    }

    @Override
    public String getId() {
        return "bongo.always";
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
    public void renderSlotContent(Minecraft mc, TaskTypeAlways content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {

    }

    @Override
    public String getTranslatedContentName(TaskTypeAlways content) {
        return "";
    }

    @Override
    public ITextComponent getContentName(TaskTypeAlways content, MinecraftServer server) {
        return new StringTextComponent("");
    }

    @Override
    public boolean shouldComplete(TaskTypeAlways element, PlayerEntity player, TaskTypeAlways compare) {
        return true;
    }

    @Override
    public CompoundNBT serializeNBT(TaskTypeAlways element) {
        return new CompoundNBT();
    }

    @Override
    public TaskTypeAlways deserializeNBT(CompoundNBT nbt) {
        return this;
    }

    @Override
    public Stream<TaskTypeAlways> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        return Stream.empty();
    }
}
