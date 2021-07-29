package io.github.noeppi_noeppi.mods.bongo.task;

import com.google.common.collect.ImmutableSet;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;

public interface TaskType<T, C> {

    Class<T> getTaskClass();
    
    Class<C> getCompareClass();

    String getId();

    default String getTranslatedName() {
        return I18n.get(getTranslationKey());
    }

    String getTranslationKey();

    void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer);

    void renderSlotContent(Minecraft mc, T content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo);

    @OnlyIn(Dist.CLIENT)
    String getTranslatedContentName(T content);

    Component getContentName(T content, MinecraftServer server);

    boolean shouldComplete(T element, Player player, C compare);

    CompoundTag serializeNBT(T element);

    T deserializeNBT(CompoundTag nbt);
    
    default void validate(T element, MinecraftServer server) {
        
    }

    default T copy(T element) {
        return element;
    }

    default void syncToClient(T element, MinecraftServer server, @Nullable ServerPlayer syncTarget) {

    }

    default Predicate<ItemStack> bongoTooltipStack(T element) {
        return stack -> false;
    }
    
    default Set<ItemStack> bookmarkStacks(T element) {
        return ImmutableSet.of();
    }
    
    default Set<ResourceLocation> bookmarkAdvancements(T element) {
        return ImmutableSet.of();
    }

    default void consumeItem(T element, C found, Player player) {

    }

    @Nullable
    default Comparator<T> getSorting() {
        return null;
    }

    Stream<T> getAllElements(MinecraftServer server, @Nullable ServerPlayer player);
}
