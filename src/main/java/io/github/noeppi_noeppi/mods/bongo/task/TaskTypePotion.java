package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.util.PotionTextureRenderCache;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.stream.Stream;

public class TaskTypePotion implements TaskTypeSimple<MobEffect> {

    public static final TaskTypePotion INSTANCE = new TaskTypePotion();

    private TaskTypePotion() {

    }

    @Override
    public Class<MobEffect> getTaskClass() {
        return MobEffect.class;
    }

    @Override
    public String getId() {
        return "bongo.potion";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.task.potion.name";
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-1, -1, 0);
        GuiComponent.blit(poseStack, 0, 0, 26, 18, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, MobEffect content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        poseStack.translate(-1, -1, 0);
        RenderSystem.setShaderTexture(0, PotionTextureRenderCache.getRenderTexture(content));
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 18, 18, 18, 18);
    }

    @Override
    public String getTranslatedContentName(MobEffect content) {
        return content.getDisplayName().getString(18);
    }

    @Override
    public Component getContentName(MobEffect content, MinecraftServer server) {
        return content.getDisplayName();
    }

    @Override
    public boolean shouldComplete(MobEffect element, Player player, MobEffect compare) {
        return element == compare;
    }

    @Override
    public void consumeItem(MobEffect element, Player player) {
        player.removeEffect(element);
    }

    @Override
    public CompoundTag serializeNBT(MobEffect element) {
        CompoundTag nbt = new CompoundTag();
        Util.putByForgeRegistry(ForgeRegistries.POTIONS, nbt, "potion", element);
        return nbt;
    }

    @Override
    public MobEffect deserializeNBT(CompoundTag nbt) {
        return Util.getFromRegistry(ForgeRegistries.POTIONS, nbt, "potion");
    }

    @Nullable
    @Override
    public Comparator<MobEffect> getSorting() {
        return Comparator.comparing(MobEffect::getRegistryName, Util.COMPARE_RESOURCE);
    }

    @Override
    public Stream<MobEffect> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return ForgeRegistries.POTIONS.getValues().stream().filter(effect -> !effect.isInstantenous());
        } else {
            return player.getActiveEffects().stream().map(MobEffectInstance::getEffect);
        }
    }
}
