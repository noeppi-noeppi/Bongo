package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.torment.ModItems;
import io.github.noeppi_noeppi.mods.torment.cap.TormentData;
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

public class TaskTypeTorment implements TaskTypeSimple<Float> {

    public static final TaskTypeTorment INSTANCE = new TaskTypeTorment();

    private TaskTypeTorment() {

    }

    @Override
    public Class<Float> getTaskClass() {
        return Float.class;
    }

    @Override
    public String getId() {
        return "spooky.torment";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.spooky.torment";
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-1, -1, 0);
        GuiComponent.blit(poseStack, 0, 0, 26, 38, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Float content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ItemRenderUtil.renderItem(poseStack, buffer, new ItemStack(ModItems.glowBerrySyrup, (int) (float) content), !bigBongo);
    }

    @Override
    public String getTranslatedContentName(Float content) {
        return I18n.get("bongo.spooky.torment.reach", content);
    }

    @Override
    public Component getContentName(Float content, MinecraftServer server) {
        return new TranslatableComponent("bongo.spooky.torment.reach", content);
    }

    @Override
    public boolean shouldComplete(Float element, Player player, Float compare) {
        return compare >= element;
    }

    @Override
    public CompoundTag serializeNBT(Float element) {
        CompoundTag nbt = new CompoundTag();
        nbt.putFloat("Level", element);
        return nbt;
    }

    @Override
    public Float deserializeNBT(CompoundTag nbt) {
        return nbt.getFloat("Level");
    }

    @Override
    public Stream<Float> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return Stream.of(5f);
        } else {
            TormentData data = TormentData.get(player);
            return Stream.of(data.getTormentLevel());
        }
    }
}
