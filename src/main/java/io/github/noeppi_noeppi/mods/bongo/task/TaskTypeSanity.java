package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.stream.Stream;

public class TaskTypeSanity implements TaskType<Integer> {

    public static final TaskTypeSanity INSTANCE = new TaskTypeSanity();

    private TaskTypeSanity() {

    }

    @Override
    public Class<Integer> getTaskClass() {
        return Integer.class;
    }

    @Override
    public String getId() {
        return "spooky20.sanity";
    }

    @Override
    public String getTranslationKey() {
        return "command.paranoia.sanity.sanity";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(-1, -1, 0);
        AbstractGui.blit(matrixStack, 0, 0, 26, 58, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Integer content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        ItemStack stack = new ItemStack(Items.ROTTEN_FLESH, content);
        RenderHelperItem.renderItemGui(matrixStack, buffer, stack, 0, 0, 16, !bigBongo);
    }

    @Override
    public String getTranslatedContentName(Integer content) {
        return I18n.format("bongo.sanity", Integer.toString(content));
    }

    @Override
    public ITextComponent getContentName(Integer content, MinecraftServer server) {
        return new TranslationTextComponent("bongo.sanity", Integer.toString(content));
    }

    @Override
    public boolean shouldComplete(Integer element, PlayerEntity player, Integer compare) {
        return compare <= element;
    }

    @Override
    public CompoundNBT serializeNBT(Integer element) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("sanity", element);
        return nbt;
    }

    @Override
    public Integer deserializeNBT(CompoundNBT nbt) {
        return nbt.getInt("sanity");
    }

    @Override
    public Stream<Integer> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        return Stream.of(80);
    }
}
