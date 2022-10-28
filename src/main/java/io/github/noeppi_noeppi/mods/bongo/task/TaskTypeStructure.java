package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class TaskTypeStructure implements TaskType<ResourceLocation> {

    public static final TaskTypeStructure INSTANCE = new TaskTypeStructure();
    
    private TaskTypeStructure() {
        
    }

    @Override
    public String id() {
        return "spooky.structure";
    }

    @Override
    public Class<ResourceLocation> taskClass() {
        return ResourceLocation.class;
    }

    @Override
    public MapCodec<ResourceLocation> codec() {
        return ResourceLocation.CODEC.fieldOf("value");
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.spooky.structure.task");
    }

    @Override
    public Component contentName(ResourceLocation element, @Nullable MinecraftServer server) {
        return Component.translatable(net.minecraft.Util.makeDescriptionId("structure", element));
    }

    @Override
    public Comparator<ResourceLocation> order() {
        return Util.COMPARE_RESOURCE;
    }

    @Override
    public void validate(ResourceLocation element, MinecraftServer server) {
        if (server.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).get(element) == null) {
            throw new IllegalStateException("Structure not registered: " + element);
        }
    }

    @Override
    public Stream<ResourceLocation> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return server.registryAccess().registryOrThrow(Registry.STRUCTURE_REGISTRY).keySet().stream();
        } else {
            return Util.structures(player.getLevel(), player.blockPosition()).stream();
        }
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, ResourceLocation element, ResourceLocation compare) {
        return Objects.equals(element, compare);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        //
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, ResourceLocation element, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        //
    }
}
