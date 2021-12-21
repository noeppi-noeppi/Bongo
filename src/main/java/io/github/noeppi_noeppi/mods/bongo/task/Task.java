package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.INBTSerializable;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.function.Predicate;

public class Task implements INBTSerializable<CompoundTag> {

    public static Task empty() {
        return new Task(TaskTypeEmpty.INSTANCE, Unit.INSTANCE);
    }

    private TaskType<?, ?> type;
    private Object element;

    private  <T> Task(TaskType<T, ?> type, T element) {
        this.type = type;
        this.element = element;
        validateElementType();
    }

    public TaskType<?, ?> getType() {
        return type;
    }

    public String getTranslatedName() {
        return type.getTranslatedName();
    }

    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        type.renderSlot(mc, poseStack, buffer);
    }

    public void renderSlotContent(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        //noinspection unchecked
        ((TaskType<Object, ?>) type).renderSlotContent(mc, element, poseStack, buffer, bigBongo);
    }

    @OnlyIn(Dist.CLIENT)
    public String getTranslatedContentName() {
        //noinspection unchecked
        return ((TaskType<Object, ?>) type).getTranslatedContentName(element);
    }

    public Component getContentName(MinecraftServer server) {
        //noinspection unchecked
        return ((TaskType<Object, ?>) type).getContentName(element, server);
    }

    public void syncToClient(MinecraftServer server, @Nullable ServerPlayer syncTarget) {
        //noinspection unchecked
        ((TaskType<Object, ?>) type).syncToClient(element, server, syncTarget);
    }

    public boolean shouldComplete(Player player, Object compare) {
        if (!type.getCompareClass().isAssignableFrom(compare.getClass())) {
            return false;
        }
        //noinspection unchecked
        return ((TaskType<Object, Object>) type).shouldComplete(element, player, compare);
    }

    public void consumeItem(Player player, Object found) {
        if (type.getCompareClass().isAssignableFrom(found.getClass())) {
            //noinspection unchecked
            ((TaskType<Object, Object>) type).consumeItem(element, found, player);
        }
    }

    public Predicate<ItemStack> bongoTooltipStack() {
        //noinspection unchecked
        return ((TaskType<Object, ?>) type).bongoTooltipStack(element);
    }
    
    public Set<ItemStack> bookmarkStacks() {
        //noinspection unchecked
        return ((TaskType<Object, ?>) type).bookmarkStacks(element);
    }
    
    public Set<ResourceLocation> bookmarkAdvancements() {
        //noinspection unchecked
        return ((TaskType<Object, ?>) type).bookmarkAdvancements(element);
    }
    
    public void validate(MinecraftServer server) {
        try {
            //noinspection unchecked
            ((TaskType<Object, ?>) type).validate(element, server);
        } catch (Exception e) {
            BongoMod.getInstance().logger.error("Failed to validate task of type {}: {}", type.getId(), e.getMessage());
        }
    }

    @Override
    public CompoundTag serializeNBT() {
        @SuppressWarnings("unchecked")
        CompoundTag nbt = ((TaskType<Object, ?>) type).serializeNBT(element);
        nbt.putString("type", type.getId());
        return nbt;
    }
    
    public void deserializeNBT(CompoundTag nbt) {
        type = TaskTypes.getType(nbt.getString("type"));
        if (type == null) {
            BongoMod.getInstance().logger.error("Failed to read task: Unknown task type: {}", nbt.getString("type"));
            type = TaskTypeEmpty.INSTANCE;
            element = Unit.INSTANCE;
        } else {
            try {
                element = type.deserializeNBT(nbt);
            } catch (Exception e) {
                String typeId = type.getId();
                type = TaskTypeEmpty.INSTANCE;
                element = Unit.INSTANCE;
                BongoMod.getInstance().logger.error("Failed to read task of type {}: {}", typeId, e.getMessage());
            }
            validateElementType();
        }
    }

    public Task copy() {
        //noinspection unchecked
        return new Task((TaskType<Object, ?>) type, ((TaskType<Object, ?>) type).copy(element));
    }
    
    @Nullable
    public <T> T getElement(TaskType<T, ?> type) {
        if (this.type == type) {
            //noinspection unchecked
            return (T) element;
        } else {
            return null;
        }
    }
    
    private void validateElementType() {
        if (!type.getTaskClass().isAssignableFrom(element.getClass())) {
            throw new IllegalStateException("Can't create task of type " + type.getId() + " with element of type " + element.getClass());
        }
    }
}
