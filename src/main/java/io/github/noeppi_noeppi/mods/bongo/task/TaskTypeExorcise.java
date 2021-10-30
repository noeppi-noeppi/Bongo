package io.github.noeppi_noeppi.mods.bongo.task;

import com.cartoonishvillain.eeriehauntings.Register;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
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

public class TaskTypeExorcise implements TaskTypeSimple<Boolean> {

    public static TaskTypeExorcise INSTANCE = new TaskTypeExorcise();
    
    private TaskTypeExorcise() {
        
    }
    
    @Override
    public Class<Boolean> getTaskClass() {
        return Boolean.class;
    }

    @Override
    public String getId() {
        return "spooky.exorcise";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.spooky.exorcise";
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-1, -1, 0);
        GuiComponent.blit(poseStack, 0, 0, 26, 38, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Boolean content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ItemRenderUtil.renderItem(poseStack, buffer, new ItemStack(Register.OLDRADIO.get()), !bigBongo);
    }

    @Override
    public String getTranslatedContentName(Boolean content) {
        return I18n.get(content ? "bongo.spooky.exorcise.needstool" : "bongo.spooky.exorcise.expell");
    }

    @Override
    public Component getContentName(Boolean content, MinecraftServer server) {
        return new TranslatableComponent(content ? "bongo.spooky.exorcise.needstool" : "bongo.spooky.exorcise.expell");
    }

    @Override
    public boolean shouldComplete(Boolean element, Player player, Boolean compare) {
        return !element || compare;
    }

    @Override
    public CompoundTag serializeNBT(Boolean element) {
        CompoundTag nbt = new CompoundTag();
        nbt.putBoolean("CorrectTool", element);
        return nbt;
    }

    @Override
    public Boolean deserializeNBT(CompoundTag nbt) {
        return nbt.getBoolean("CorrectTool");
    }

    @Override
    public Stream<Boolean> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        return Stream.of(true, false);
    }
}
