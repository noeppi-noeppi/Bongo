package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.texture.MissingTextureSprite;
import net.minecraft.client.renderer.texture.Texture;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.biome.Biome;
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
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        AbstractGui.blit(matrixStack, 0, 0, 18, 0, 18, 18, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Biome content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        ResourceLocation biomeTexture;
        if (content.getRegistryName() != null) {
            biomeTexture = new ResourceLocation(content.getRegistryName().getNamespace(), "textures/icon/biome/" + content.getRegistryName().getPath() + ".png");
        } else {
            biomeTexture = FALLBACK_TEXTURE;
        }
        mc.getTextureManager().bindTexture(biomeTexture);
        Texture texture = mc.getTextureManager().getTexture(biomeTexture);
        if (texture == null || texture.getGlTextureId() == MissingTextureSprite.getDynamicTexture().getGlTextureId())
            mc.getTextureManager().bindTexture(FALLBACK_TEXTURE);
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 16, 16, 16, 16);
    }

    @Override
    public String getTranslatedContentName(Biome content) {
        return new TranslationTextComponent(net.minecraft.util.Util.makeTranslationKey("biome", ForgeRegistries.BIOMES.getKey(content))).getStringTruncated(18);
    }

    @Override
    public ITextComponent getContentName(Biome content, MinecraftServer server) {
        return new TranslationTextComponent(net.minecraft.util.Util.makeTranslationKey("biome", ForgeRegistries.BIOMES.getKey(content)));
    }

    @Override
    public boolean shouldComplete(Biome element, PlayerEntity player, Biome compare) {
        return element == compare;
    }

    @Override
    public CompoundNBT serializeNBT(Biome element) {
        CompoundNBT nbt = new CompoundNBT();
        Util.putByForgeRegistry(ForgeRegistries.BIOMES, nbt, "biome", element);
        return nbt;
    }

    @Override
    public Biome deserializeNBT(CompoundNBT nbt) {
        return Util.getFromRegistry(ForgeRegistries.BIOMES, nbt, "biome");
    }

    @Nullable
    @Override
    public Comparator<Biome> getSorting() {
        return Comparator.comparing(Biome::getRegistryName, Util.COMPARE_RESOURCE);
    }

    @Override
    public Stream<Biome> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return ForgeRegistries.BIOMES.getValues().stream();
        } else {
            return Stream.of(ForgeRegistries.BIOMES.getValue(player.getEntityWorld().func_241828_r().getRegistry(Registry.BIOME_KEY).getKey(player.getEntityWorld().getBiome(player.getPosition()))));
        }
    }
}
