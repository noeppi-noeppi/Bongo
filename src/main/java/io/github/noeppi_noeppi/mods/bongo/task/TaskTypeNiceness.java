package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.championash5357.naughtyornice.api.capability.CapabilityInstances;
import io.github.championash5357.naughtyornice.api.capability.INiceness;
import io.github.championash5357.naughtyornice.common.init.GeneralRegistrar;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.stream.Stream;

public class TaskTypeNiceness implements TaskType<Integer> {

    public static final TaskTypeNiceness INSTANCE = new TaskTypeNiceness();

    private TaskTypeNiceness() {

    }

    @Override
    public Class<Integer> getTaskClass() {
        return Integer.class;
    }

    @Override
    public String getId() {
        return "winter.niceness";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.task.niceness.name";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(-1, -1, 0);
        AbstractGui.blit(matrixStack, 0, 0, 26, 18, 20, 20, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, Integer content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        ItemStack stack = new ItemStack(GeneralRegistrar.PRESENT.get());
        stack.setCount(content);
        RenderHelperItem.renderItemGui(matrixStack, buffer, stack, 0, 0, 16, !bigBongo);
    }

    @Override
    public String getTranslatedContentName(Integer content) {
        return I18n.format("bongo.task.niceness.description", Integer.toString(content));
    }

    @Override
    public ITextComponent getContentName(Integer content, MinecraftServer server) {
        return new TranslationTextComponent("bongo.task.niceness.description", Integer.toString(content));
    }

    @Override
    public boolean shouldComplete(Integer element, PlayerEntity player, Integer compare) {
        return compare >= element;
    }

    @Override
    public CompoundNBT serializeNBT(Integer element) {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("niceness", element);
        return nbt;
    }

    @Override
    public Integer deserializeNBT(CompoundNBT nbt) {
        return nbt.getInt("niceness");
    }

    @Override
    public Stream<Integer> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        if (player == null) {
            return Stream.of(10);
        } else {
            Optional<INiceness> niceness = player.getCapability(CapabilityInstances.NICENESS_CAPABILITY).resolve();
            return niceness.map(iNiceness -> Stream.of((int) Math.round(iNiceness.getNiceness()))).orElseGet(() -> Stream.of(0));
        }
    }
}
