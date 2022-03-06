package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.MissingTextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.stream.Stream;

public class TaskTypeBiome implements TaskTypeSimple<Biome> {

    public static final TaskTypeBiome INSTANCE = new TaskTypeBiome();

    private static final ResourceLocation FALLBACK_TEXTURE = new ResourceLocation("minecraft", "textures/icon/biome/forest.png");

    private TaskTypeBiome() {

    }

    @Override
    public Class<Biome> getTaskClass() {
        return Biome.class;
    }

    @Override
    public String getId() {
        return "bongo.biome";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.task.biome.name";
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        GuiComponent.blit(poseStack, 0, 0, 18, 0, 18, 18, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Biome content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ResourceLocation biomeTexture;
        if (content.getRegistryName() != null) {
            biomeTexture = new ResourceLocation(content.getRegistryName().getNamespace(), "textures/icon/biome/" + content.getRegistryName().getPath() + ".png");
        } else {
            biomeTexture = FALLBACK_TEXTURE;
        }
        RenderSystem.setShaderTexture(0, biomeTexture);
        AbstractTexture texture = mc.getTextureManager().getTexture(biomeTexture);
        //noinspection ConstantConditions
        if (texture == null || texture.getId() == MissingTextureAtlasSprite.getTexture().getId())
            RenderSystem.setShaderTexture(0, FALLBACK_TEXTURE);
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public String getTranslatedContentName(Biome content) {
        return new TranslatableComponent(net.minecraft.Util.makeDescriptionId("biome", ForgeRegistries.BIOMES.getKey(content))).getString(18);
    }

    @Override
    public Component getContentName(Biome content, MinecraftServer server) {
        return new TranslatableComponent(net.minecraft.Util.makeDescriptionId("biome", ForgeRegistries.BIOMES.getKey(content)));
    }

    @Override
    public boolean shouldComplete(Biome element, Player player, Biome compare) {
        return element == compare;
    }

    @Override
    public CompoundTag serializeNBT(Biome element) {
        CompoundTag nbt = new CompoundTag();
        Util.putByForgeRegistry(ForgeRegistries.BIOMES, nbt, "biome", element);
        return nbt;
    }

    @Override
    public Biome deserializeNBT(CompoundTag nbt) {
        return Util.getFromRegistry(ForgeRegistries.BIOMES, nbt, "biome");
    }

    @Nullable
    @Override
    public Comparator<Biome> getSorting() {
        return Comparator.comparing(Biome::getRegistryName, Util.COMPARE_RESOURCE);
    }

    @Override
    public Stream<Biome> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return ForgeRegistries.BIOMES.getValues().stream();
        } else {
            try {
                return Stream.of(ForgeRegistries.BIOMES.getValue(player.getCommandSenderWorld().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getKey(player.getCommandSenderWorld().getBiome(player.blockPosition()).value())));
            } catch (Exception e) {
                e.printStackTrace();
                return Stream.empty();
            }
        }
    }
}
