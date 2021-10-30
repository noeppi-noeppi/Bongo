package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
import melonslise.spook.common.capability.ISanity;
import melonslise.spook.common.init.SpookCapabilities;
import melonslise.spook.common.init.SpookItems;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TaskTypeMist implements TaskTypeSimple<Float> {

    public static final TaskTypeMist INSTANCE = new TaskTypeMist();

    private TaskTypeMist() {

    }

    @Override
    public Class<Float> getTaskClass() {
        return Float.class;
    }

    @Override
    public String getId() {
        return "spooky.mist";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.spooky.mist";
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-1, -1, 0);
        GuiComponent.blit(poseStack, 0, 0, 26, 38, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Float content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ItemRenderUtil.renderItem(poseStack, buffer, new ItemStack(SpookItems.OLD_NOTES.get(), (int) (float) content), !bigBongo);
    }

    @Override
    public String getTranslatedContentName(Float content) {
        return I18n.get("bongo.spooky.mist.reach", content);
    }

    @Override
    public Component getContentName(Float content, MinecraftServer server) {
        return new TranslatableComponent("bongo.spooky.mist.reach", content);
    }

    @Override
    public boolean shouldComplete(Float element, Player player, Float compare) {
        return compare <= element;
    }

    @Override
    public CompoundTag serializeNBT(Float element) {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Sanity", element);
        return nbt;
    }

    @Override
    public Float deserializeNBT(CompoundTag nbt) {
        return nbt.getFloat("Sanity");
    }

    @Override
    public Stream<Float> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return Stream.of(60f);
        } else {
            ISanity sanity = player.getCapability(SpookCapabilities.SANITY).resolve().orElse(null);
            return Stream.of(sanity == null ? 60f : sanity.get());
        }
    }
}
