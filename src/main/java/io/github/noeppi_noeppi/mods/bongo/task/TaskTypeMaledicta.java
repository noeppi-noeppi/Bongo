package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.util.Unit;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.stream.Stream;

public class TaskTypeMaledicta implements TaskType<Unit> {

    public static final TaskTypeMaledicta INSTANCE = new TaskTypeMaledicta();

    private TaskTypeMaledicta() {
        
    }

    @Override
    public String id() {
        return "spooky.maledicta";
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
        return Component.translatable("bongo.spooky.maledicta.task");
    }

    @Override
    public Component contentName(Unit element, @Nullable MinecraftServer server) {
        return Component.translatable("bongo.spooky.maledicta.desc");
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FormattedCharSequence renderDisplayName(Minecraft mc, Unit element) {
        return this.contentName(element, null).getVisualOrderText();
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
        return true;
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
