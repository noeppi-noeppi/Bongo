package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.mods.bongo.util.RenderEntityCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;

public class TaskTypeEntity implements TaskType<EntityType<?>> {

    public static final TaskTypeEntity INSTANCE = new TaskTypeEntity();

    private TaskTypeEntity() {

    }

    @Override
    public Class<EntityType<?>> getTaskClass() {
        //noinspection unchecked
        return (Class<EntityType<?>>) (Object) EntityType.class;
    }

    @Override
    public String getId() {
        return "bongo.entity";
    }

    @Override
    public String getTranslationKey() {
        return "bongo.task.entity.name";
    }

    @Override
    public void renderSlot(Minecraft mc, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        matrixStack.translate(-2, -2, 0);
        matrixStack.scale(22 / 26f, 22 / 26f, 1);
        AbstractGui.blit(matrixStack, 0, 0, 0, 44, 26, 26, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, EntityType<?> content, MatrixStack matrixStack, IRenderTypeBuffer buffer) {
        @SuppressWarnings("unchecked")
        EntityRenderer<Entity> render = (EntityRenderer<Entity>) mc.getRenderManager().renderers.get(content);
        if (render != null) {
            Entity entity = RenderEntityCache.getRenderEntity(mc, content);
            AxisAlignedBB bb = entity.getRenderBoundingBox();
            float scale = (float) Math.min(Math.min(8d / bb.getXSize(), 16d / bb.getYSize()), 8d / bb.getZSize());
            matrixStack.translate(8, 16, 100);
            matrixStack.scale(scale, scale, scale);
            matrixStack.rotate(Vector3f.ZP.rotationDegrees(180));
            matrixStack.rotate(Vector3f.YP.rotationDegrees(45));
            matrixStack.rotate(Vector3f.XP.rotationDegrees(2));
            render.render(entity, 0, 0, matrixStack, buffer, 100);
        }
    }

    @Override
    public String getTranslatedContentName(EntityType<?> content) {
        return I18n.format(content.getTranslationKey());
    }

    @Override
    public ITextComponent getContentName(EntityType<?> content, MinecraftServer server) {
        return new TranslationTextComponent(content.getTranslationKey());
    }

    @Override
    public boolean shouldComplete(EntityType<?> element, PlayerEntity player, EntityType<?> compare) {
        return element == compare;
    }

    @Override
    public ItemStack bongoTooltipStack(EntityType<?> element) {
        Item item = SpawnEggItem.getEgg(element);
        if (item == null) {
            return ItemStack.EMPTY;
        } else {
            return new ItemStack(item);
        }
    }

    @Override
    public CompoundNBT serializeNBT(EntityType<?> element) {
        CompoundNBT nbt = new CompoundNBT();
        //noinspection ConstantConditions
        nbt.putString("entity", element.getRegistryName().toString());
        return nbt;
    }

    @Override
    public EntityType<?> deserializeNBT(CompoundNBT nbt) {
        return ForgeRegistries.ENTITIES.getValue(new ResourceLocation(nbt.getString("entity")));
    }
}