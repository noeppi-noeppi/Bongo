package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.render.RenderHelper;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TaskTypePainting extends RegistryTaskType<PaintingVariant> {

    public static final TaskTypePainting INSTANCE = new TaskTypePainting();
    
    private TaskTypePainting() {
        super(PaintingVariant.class, ForgeRegistries.PAINTING_VARIANTS);
    }

    @Override
    public String id() {
        return "spooky.painting";
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.spooky.painting.task");
    }

    @Override
    public Component contentName(PaintingVariant element, @Nullable MinecraftServer server) {
        ResourceLocation key = ForgeRegistries.PAINTING_VARIANTS.getKey(element);
        if (key == null) return Component.literal("unknown");
        return Component.literal("minecraft".equals(key.getNamespace()) || "funkyframes".equals(key.getNamespace()) ? key.getPath() : key.toString());
    }

    @Override
    public Stream<PaintingVariant> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return ForgeRegistries.PAINTING_VARIANTS.getValues().stream();
        } else {
            return player.getCommandSenderWorld().getEntities(player, new AABB(player.getX() - 10, player.getY() - 5, player.getZ() - 10, player.getX() + 10, player.getY() + 5, player.getZ() + 10)).stream()
                    .filter(e -> e instanceof Painting)
                    .map(e -> (Painting) e)
                    .map(Painting::getVariant)
                    .map(Holder::value);
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, PaintingVariant element, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ResourceLocation key = ForgeRegistries.PAINTING_VARIANTS.getKey(element);
        int maxSize = Math.max(element.getWidth(), element.getHeight());
        int width = Math.round((element.getWidth() / (float) maxSize) * 18);
        int height = Math.round((element.getHeight() / (float) maxSize) * 18);
        int xOff = (18 - width) / 2;
        int yOff = (18 - height) / 2;
        if (key == null) return;
        RenderHelper.resetColor();
        RenderSystem.setShaderTexture(0, new ResourceLocation(key.getNamespace(), "textures/painting/" + key.getPath() + ".png"));
        GuiComponent.blit(poseStack, xOff - 1, yOff - 1, 0, 0, width, height, width, height);
    }
}
