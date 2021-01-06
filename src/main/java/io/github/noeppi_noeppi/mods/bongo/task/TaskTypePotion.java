package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.util.PotionTextureRenderCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.stream.Stream;

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
        matrixStack.translate(-1, -1, 0);
        Minecraft.getInstance().getTextureManager().bindTexture(PotionTextureRenderCache.getRenderTexture(content));
        AbstractGui.blit(matrixStack, 0, 0, 0, 0, 18, 18, 18, 18);
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
    public void consumeItem(Effect element, PlayerEntity player) {
        player.removePotionEffect(element);
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

    @Override
    public Stream<Effect> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return ForgeRegistries.POTIONS.getValues().stream().filter(effect -> !effect.isInstant());
        } else {
            return player.getActivePotionEffects().stream().map(EffectInstance::getPotion);
        }
    }
}
