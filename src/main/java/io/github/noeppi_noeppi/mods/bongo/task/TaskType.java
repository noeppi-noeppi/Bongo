package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.Codec;
import io.github.noeppi_noeppi.mods.bongo.util.Highlight;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Set;
import java.util.stream.Stream;

public interface TaskType<T> {

    String id();
    Class<T> taskClass();
    Codec<T> codec();
    Component name();
    Component contentName(T element, @Nullable MinecraftServer server);
    Comparator<T> order();
    
    default void validate(T element, MinecraftServer server) {}
    default T copy(T element) { return element; }
    default void sync(T element, MinecraftServer server, @Nullable ServerPlayer target) {}
    Stream<T> listElements(MinecraftServer server, @Nullable ServerPlayer player);
    
    boolean shouldComplete(ServerPlayer player, T element, T compare);
    default void consume(ServerPlayer player, T element, T found) {}
    default Set<Highlight<?>> highlight(T element) { return Set.of(); }
    
    @OnlyIn(Dist.CLIENT) void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer);
    @OnlyIn(Dist.CLIENT) void renderSlotContent(Minecraft mc, T content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo);
}
