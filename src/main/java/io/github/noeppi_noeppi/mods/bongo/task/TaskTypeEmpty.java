package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TaskTypeEmpty implements TaskType<TaskTypeEmpty, Void> {

    public static final TaskTypeEmpty INSTANCE = new TaskTypeEmpty();

    private TaskTypeEmpty() {

    }

    @Override
    public Class<TaskTypeEmpty> getTaskClass() {
        return TaskTypeEmpty.class;
    }

    @Override
    public Class<Void> getCompareClass() {
        return Void.class;
    }

    @Override
    public String getId() {
        return "bongo.empty";
    }

    @Override
    public String getTranslatedName() {
        return "Empty";
    }

    @Override
    public String getTranslationKey() {
        return "Empty";
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {

    }

    @Override
    public void renderSlotContent(Minecraft mc, TaskTypeEmpty content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {

    }

    @Override
    public String getTranslatedContentName(TaskTypeEmpty content) {
        return "";
    }

    @Override
    public Component getContentName(TaskTypeEmpty content, MinecraftServer server) {
        return new TextComponent("");
    }

    @Override
    public boolean shouldComplete(TaskTypeEmpty element, Player player, Void compare) {
        return false;
    }

    @Override
    public CompoundTag serializeNBT(TaskTypeEmpty element) {
        return new CompoundTag();
    }

    @Override
    public TaskTypeEmpty deserializeNBT(CompoundTag nbt) {
        return this;
    }

    @Override
    public Stream<TaskTypeEmpty> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        return Stream.empty();
    }
}
