package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.MapCodec;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.registries.Registries;
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

// This is a datapack registry, so we can't use Biome here.
public class TaskTypeBiome implements TaskType<ResourceLocation> {

    public static final TaskTypeBiome INSTANCE = new TaskTypeBiome();

    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/icon/biome/forest.png");

    private TaskTypeBiome() {

    }

    @Override
    public String id() {
        return "bongo.biome";
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
        return Component.translatable("bongo.task.biome.name");
    }

    @Override
    public Component contentName(ResourceLocation element, @Nullable MinecraftServer server) {
        return Component.translatable(net.minecraft.Util.makeDescriptionId("biome", element));
    }

    @Override
    public Comparator<ResourceLocation> order() {
        return Util.COMPARE_RESOURCE;
    }

    @Override
    public void validate(ResourceLocation element, MinecraftServer server) {
        if (server.registryAccess().registryOrThrow(Registries.BIOME).get(element) == null) {
            throw new IllegalStateException("Biome not registered: " + element);
        }
    }

    @Override
    public Stream<ResourceLocation> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return server.registryAccess().registryOrThrow(Registries.BIOME).keySet().stream();
        } else {
            return Util.biome(player.serverLevel(), player.blockPosition()).stream();
        }
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, ResourceLocation element, ResourceLocation compare) {
        return Objects.equals(element, compare);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, GuiGraphics graphics) {
        graphics.blit(RenderOverlay.BINGO_SLOTS_TEXTURE, 0, 0, 18, 0, 18, 18, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, GuiGraphics graphics, ResourceLocation element, boolean bigBongo) {
        ResourceLocation biomeTexture;
        if (element != null) {
            biomeTexture = new ResourceLocation(element.getNamespace(), "textures/icon/biome/" + element.getPath() + ".png");
        } else {
            biomeTexture = FALLBACK_TEXTURE;
        }
        RenderSystem.setShaderTexture(0, biomeTexture);
        AbstractTexture texture = mc.getTextureManager().getTexture(biomeTexture);
        //noinspection ConstantConditions
        if (texture == null || texture.getId() == MissingTextureAtlasSprite.getTexture().getId()) {
            biomeTexture = FALLBACK_TEXTURE;
        }
        graphics.blit(biomeTexture, 0, 0, 0, 0, 16, 16, 16, 16);
    }
}
