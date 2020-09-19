package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.mods.bongo.render.RenderHelper;
import io.github.noeppi_noeppi.mods.bongo.util.PotionItemRenderCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class TaskTypePotion implements TaskType<Effect> {

    public static final TaskTypePotion INSTANCE = new TaskTypePotion();

    private TaskTypePotion() {

    }

    @Override
    public Class<Effect> getTaskClass() {
        return Effect.class;
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
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(-1, -1, 0);
        AbstractGui.blit(matrixStack, 0, 0, 26, 18, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Effect content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        RenderHelper.renderItemGui(matrixStack, buffer, PotionItemRenderCache.getRenderStack(content), -2, -2, 19, false);
    }

    @Override
    public String getTranslatedContentName(Effect content) {
        return content.getDisplayName().getStringTruncated(18);
    }

    @Override
    public ITextComponent getContentName(Effect content, MinecraftServer server) {
        return content.getDisplayName();
    }

    @Override
    public boolean shouldComplete(Effect element, PlayerEntity player, Effect compare) {
        return element == compare;
    }

    @Override
    public CompoundNBT serializeNBT(Effect element) {
        CompoundNBT nbt = new CompoundNBT();
        //noinspection ConstantConditions
        nbt.putString("potion", element.getRegistryName().toString());
        return nbt;
    }

    @Override
    public Effect deserializeNBT(CompoundNBT nbt) {
        return ForgeRegistries.POTIONS.getValue(new ResourceLocation(nbt.getString("potion")));
    }
}
