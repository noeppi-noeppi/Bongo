package io.github.noeppi_noeppi.mods.bongo.task;

import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.PotionTextureRenderCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
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
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, GuiGraphics graphics) {
        graphics.pose().translate(-1, -1, 0);
        graphics.blit(RenderOverlay.BINGO_SLOTS_TEXTURE, 0, 0, 26, 18, 20, 20, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, GuiGraphics graphics, MobEffect element, boolean bigBongo) {
        graphics.pose().translate(-1, -1, 0);
        graphics.blit(PotionTextureRenderCache.getRenderTexture(element), 0, 0, 0, 0, 18, 18, 18, 18);
    }
}
