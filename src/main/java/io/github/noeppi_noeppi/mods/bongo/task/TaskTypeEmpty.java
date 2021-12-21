package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TaskTypeEmpty implements TaskType<Unit, Void> {

    public static final TaskTypeEmpty INSTANCE = new TaskTypeEmpty();

    private TaskTypeEmpty() {

    }

    @Override
    public Class<Unit> getTaskClass() {
        return Unit.class;
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
    public void renderSlotContent(Minecraft mc, Unit content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {

    }

    @Override
    public String getTranslatedContentName(Unit content) {
        return "";
    }

    @Override
    public Component getContentName(Unit content, MinecraftServer server) {
        return new TextComponent("");
    }

    @Override
    public boolean shouldComplete(Unit element, Player player, Void compare) {
        return false;
    }

    @Override
    public CompoundTag serializeNBT(Unit element) {
        return new CompoundTag();
    }

    @Override
    public Unit deserializeNBT(CompoundTag nbt) {
        return Unit.INSTANCE;
    }

    @Override
    public Stream<Unit> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        return Stream.of(Unit.INSTANCE);
    }
}
