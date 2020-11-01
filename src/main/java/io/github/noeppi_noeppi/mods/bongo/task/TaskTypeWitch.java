package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.mods.bongo.util.RenderEntityCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;

import javax.annotation.Nullable;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class TaskTypeWitch implements TaskType<TaskTypeWitch> {

    public static final TaskTypeWitch INSTANCE = new TaskTypeWitch();

    private TaskTypeWitch() {

    }

    @Override
    public Class<TaskTypeWitch> getTaskClass() {
        return TaskTypeWitch.class;
    }

    @Override
    public String getId() {
        return "spooky20.witch";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.witch1";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(-2, -2, 0);
        matrixStack.scale(22 / 26f, 22 / 26f, 1);
        AbstractGui.blit(matrixStack, 0, 0, 0, 44, 26, 26, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, TaskTypeWitch content, MatrixStack matrixStack, IRenderTypeBuffer buffer, boolean bigBongo) {
        EntityType<?> type = (ClientTickHandler.ticksInGame / 20) % 2 == 0 ? EntityType.WITCH : EntityType.CAT;
        //noinspection unchecked
        EntityRenderer<net.minecraft.entity.Entity> render = (EntityRenderer<Entity>) mc.getRenderManager().renderers.get(type);
        if (render != null) {
            Entity entity = RenderEntityCache.getRenderEntity(mc, type);
            AxisAlignedBB bb = entity.getRenderBoundingBox();
            float scale = (float) Math.min(Math.min(8d / bb.getXSize(), 16d / bb.getYSize()), 8d / bb.getZSize());
            matrixStack.translate(8, 16, 50);
            matrixStack.scale(scale, scale, scale);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(45));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(2));
            entity.ticksExisted = ClientTickHandler.ticksInGame;
            render.render(entity, 0, mc.getRenderPartialTicks(), matrixStack, buffer, 100);
        }
    }

    @Override
    public String getTranslatedContentName(TaskTypeWitch content) {
        return I18n.format("bongo.witch2");
    }

    @Override
    public ITextComponent getContentName(TaskTypeWitch content, MinecraftServer server) {
        return new TranslationTextComponent("bongo.witch2");
    }

    @Override
    public boolean shouldComplete(TaskTypeWitch element, PlayerEntity player, TaskTypeWitch compare) {
        return true;
    }

    @Override
    public CompoundNBT serializeNBT(TaskTypeWitch element) {
        return new CompoundNBT();
    }

    @Override
    public TaskTypeWitch deserializeNBT(CompoundNBT nbt) {
        return this;
    }

    @Override
    public Stream<TaskTypeWitch> getAllElements(MinecraftServer server, @Nullable ServerPlayerEntity player) {
        return Stream.of(this);
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(TaskTypeWitch element) {
        return stack -> stack.getItem() == Items.WITCH_SPAWN_EGG;
    }
}
