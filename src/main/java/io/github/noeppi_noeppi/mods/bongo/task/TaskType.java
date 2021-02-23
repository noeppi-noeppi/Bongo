package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface TaskType<T> {

    Class<T> getTaskClass();

    String getId();

    default String getTranslatedName() {
        return I18n.format(getTranslationKey());
    }

    String getTranslationKey();

    void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer);

    void renderSlotContent(Minecraft mc, T content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo);

    @OnlyIn(Dist.CLIENT)
    String getTranslatedContentName(T content);

    ITextComponent getContentName(T content, MinecraftServer server);

    boolean shouldComplete(T element, PlayerEntity player, T compare);

    CompoundNBT serializeNBT(T element);

    T deserializeNBT(CompoundNBT nbt);

    default T copy(T element) {
        return element;
    }

    default void syncToClient(T element, MinecraftServer server, @Nullable ServerPlayerEntity syncTarget) {

    }

    default Predicate<ItemStack> bongoTooltipStack(T element) {
        return stack -> false;
    }

    default void consumeItem(T element, PlayerEntity player) {

    }

    @Nullable
    default Comparator<T> getSorting() {
        return null;
    }

    Stream<T> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player);
}
