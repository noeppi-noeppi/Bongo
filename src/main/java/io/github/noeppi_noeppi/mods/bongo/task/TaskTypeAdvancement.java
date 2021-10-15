package io.github.noeppi_noeppi.mods.bongo.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.util.ClientAdvancementInfo;
import io.github.noeppi_noeppi.mods.bongo.util.ItemRenderUtil;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeAdvancement implements TaskTypeSimple<ResourceLocation> {

    public static final TaskTypeAdvancement INSTANCE = new TaskTypeAdvancement();

    private TaskTypeAdvancement() {

    }

    @Override
    public Class<ResourceLocation> getTaskClass() {
        return ResourceLocation.class;
    }

    @Override
    public String getId() {
        return "bongo.advancement";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.task.advancement.name";
    }

    @Override
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-1, -1, 0);
        poseStack.scale(20 / 26f, 20 / 26f, 1);
        GuiComponent.blit(poseStack, 0, 0, 0, 18, 26, 26, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, ResourceLocation content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        ItemStack icon = ClientAdvancementInfo.getDisplay(content);
        ItemRenderUtil.renderItem(poseStack, buffer, icon, false);
    }

    @Override
    public String getTranslatedContentName(ResourceLocation content) {
        return ClientAdvancementInfo.getTranslation(content).getString(18);
    }

    @Override
    public Component getContentName(ResourceLocation content, MinecraftServer server) {
        Advancement advancement = server.getAdvancements().getAdvancement(content);
        if (advancement == null) {
            return new TranslatableComponent("bongo.task.advancement.invalid");
        } else {
            return advancement.getChatComponent();
        }
    }

    @Override
    public boolean shouldComplete(ResourceLocation element, Player player, ResourceLocation compare) {
        return element.equals(compare);
    }

    @Override
    public void syncToClient(ResourceLocation element, MinecraftServer server, @Nullable ServerPlayer syncTarget) {
        Advancement advancement = server.getAdvancements().getAdvancement(element);
        if (advancement != null) {
            if (syncTarget == null) {
                BongoMod.getNetwork().syncAdvancement(advancement);
            } else {
                BongoMod.getNetwork().syncAdvancementTo(advancement, syncTarget);
            }
        }
    }

    @Override
    public CompoundTag serializeNBT(ResourceLocation element) {
        CompoundTag nbt = new CompoundTag();
        nbt.putString("advancement", element.toString());
        return nbt;
    }

    @Override
    public ResourceLocation deserializeNBT(CompoundTag nbt) {
        return Util.getLocationFor(nbt, "advancement");
    }

    @Override
    public void validate(ResourceLocation element, MinecraftServer server) {
        if (server.getAdvancements().getAdvancement(element) == null) {
            throw new IllegalStateException("Advancement not found: " + element);
        }
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(ResourceLocation element) {
        return ClientAdvancementInfo.getTooltipItem(element);
    }

    @Override
    public Set<ResourceLocation> bookmarkAdvancements(ResourceLocation element) {
        return ImmutableSet.of(element);
    }

    @Nullable
    @Override
    public Comparator<ResourceLocation> getSorting() {
        return Util.COMPARE_RESOURCE;
    }

    @Override
    public Stream<ResourceLocation> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return server.getAdvancements().getAllAdvancements().stream().filter(adv -> adv.getDisplay() != null).map(Advancement::getId);
        } else {
            return server.getAdvancements().getAllAdvancements().stream().filter(adv -> adv.getDisplay() != null).filter(adv -> player.getAdvancements().getOrStartProgress(adv).isDone()).map(Advancement::getId);
        }
    }
}
