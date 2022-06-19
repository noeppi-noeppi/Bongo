package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.util.PotionTextureRenderCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TaskTypeEffect extends RegistryTaskType<MobEffect> {

    public static final TaskTypeEffect INSTANCE = new TaskTypeEffect();

    private TaskTypeEffect() {
        super(MobEffect.class, ForgeRegistries.MOB_EFFECTS);
    }

    @Override
    public String id() {
        return "bongo.effect";
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.task.potion.name");
    }

    @Override
    public Component contentName(MobEffect element, @Nullable MinecraftServer server) {
        return element.getDisplayName();
    }

    @Override
    public Stream<MobEffect> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return ForgeRegistries.MOB_EFFECTS.getValues().stream().filter(effect -> !effect.isInstantenous());
        } else {
            return player.getActiveEffects().stream().map(MobEffectInstance::getEffect);
        }
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-1, -1, 0);
        GuiComponent.blit(poseStack, 0, 0, 26, 18, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, MobEffect element, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        poseStack.translate(-1, -1, 0);
        RenderSystem.setShaderTexture(0, PotionTextureRenderCache.getRenderTexture(element));
        GuiComponent.blit(poseStack, 0, 0, 0, 0, 18, 18, 18, 18);
    }
}
