package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.mods.bongo.render.RenderHelper;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.DisplayInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.util.Constants;

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
    public void renderSlotContent(Minecraft mc, ResourceLocation content, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        ItemStack icon = new ItemStack(Items.BARRIER);

        ClientPlayNetHandler cpnh = mc.getConnection();
        if (cpnh != null) {
            Advancement advancement = cpnh.getAdvancementManager().getAdvancementList().getAdvancement(content);
            if (advancement != null) {
                DisplayInfo di = advancement.getDisplay();
                if (di != null) {
                    icon = di.getIcon();
                }
            }
        }

        RenderHelper.renderItemGui(matrixStack, buffer, icon, 0, 0, 16);
    }

    @Override
    public String getTranslatedContentName(ResourceLocation content) {
        ClientPlayNetHandler cpnh = Minecraft.getInstance().getConnection();
        if (cpnh != null) {
            Advancement advancement = cpnh.getAdvancementManager().getAdvancementList().getAdvancement(content);
            if (advancement != null) {
                DisplayInfo di = advancement.getDisplay();
                if (di != null) {
                    return di.getTitle().getStringTruncated(18);
                }
            }
        }

        return I18n.format("bongo.task.advancement.invalid");
    }

    @Override
    public ITextComponent getContentName(ResourceLocation content) {
        return new StringTextComponent("");
    }

    @Override
    public boolean shouldComplete(ResourceLocation element, PlayerEntity player, ResourceLocation compare) {
        return element.equals(compare);
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
}