package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.stream.Stream;

public class TaskTypeEmpty implements TaskType<Unit> {

    public static final TaskTypeEmpty INSTANCE = new TaskTypeEmpty();

    private TaskTypeEmpty() {

    }

    @Override
    public String id() {
        return "bongo.empty";
    }

    @Override
    public Class<Unit> taskClass() {
        return Unit.class;
    }

    @Override
    public MapCodec<Unit> codec() {
        return MapCodec.unit(Unit.INSTANCE);
    }

    @Override
    public Component name() {
        return Component.literal("Empty");
    }

    @Override
    public Component contentName(Unit element, @Nullable MinecraftServer server) {
        return Component.empty();
    }

    @Override
    public Comparator<Unit> order() {
        return Comparator.comparing(Unit::ordinal);
    }

    @Override
    public Stream<Unit> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        return Stream.of(Unit.INSTANCE);
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, Unit element, Unit compare) {
        return false;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        //
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, Unit element, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        //
    }
}
