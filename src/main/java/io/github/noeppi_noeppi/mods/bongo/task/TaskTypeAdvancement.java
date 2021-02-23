package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.util.ClientAdvancementInfo;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.advancements.Advancement;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeAdvancement implements TaskType<ResourceLocation> {

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
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(-1, -1, 0);
        matrixStack.scale(20 / 26f, 20 / 26f, 1);
        AbstractGui.blit(matrixStack, 0, 0, 0, 18, 26, 26, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, ResourceLocation content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        ItemStack icon = ClientAdvancementInfo.getDisplay(content);
        RenderHelperItem.renderItemGui(matrixStack, buffer, icon, 0, 0, 16, false);
    }

    @Override
    public String getTranslatedContentName(ResourceLocation content) {
        return ClientAdvancementInfo.getTranslation(content).getStringTruncated(18);
    }

    @Override
    public ITextComponent getContentName(ResourceLocation content, MinecraftServer server) {
        Advancement advancement = server.getAdvancementManager().getAdvancement(content);
        if (advancement == null) {
            return new TranslationTextComponent("bongo.task.advancement.invalid");
        } else {
            return advancement.getDisplayText();
        }
    }

    @Override
    public boolean shouldComplete(ResourceLocation element, PlayerEntity player, ResourceLocation compare) {
        return element.equals(compare);
    }

    @Override
    public void syncToClient(ResourceLocation element, MinecraftServer server, @Nullable ServerPlayerEntity syncTarget) {
        Advancement advancement = server.getAdvancementManager().getAdvancement(element);
        if (advancement != null) {
            if (syncTarget == null) {
                BongoMod.getNetwork().syncAdvancement(advancement);
            } else {
                BongoMod.getNetwork().syncAdvancementTo(advancement, syncTarget);
            }
        }
    }

    @Override
    public CompoundNBT serializeNBT(ResourceLocation element) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putString("advancement", element.toString());
        return nbt;
    }

    @Override
    public ResourceLocation deserializeNBT(CompoundNBT nbt) {
        if (nbt.contains("advancement", Constants.NBT.TAG_STRING)) {
            return new ResourceLocation(nbt.getString("advancement"));
        } else {
            return new ResourceLocation("minecraft", "invalid");
        }
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(ResourceLocation element) {
        return ClientAdvancementInfo.getTooltipItem(element);
    }

    @Nullable
    @Override
    public Comparator<ResourceLocation> getSorting() {
        return Util.COMPARE_RESOURCE;
    }

    @Override
    public Stream<ResourceLocation> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return server.getAdvancementManager().getAllAdvancements().stream().filter(adv -> adv.getDisplay() != null).map(Advancement::getId);
        } else {
            return server.getAdvancementManager().getAllAdvancements().stream().filter(adv -> adv.getDisplay() != null).filter(adv -> player.getAdvancements().getProgress(adv).isDone()).map(Advancement::getId);
        }
    }
}