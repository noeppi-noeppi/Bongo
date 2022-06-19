package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import org.moddingx.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.mods.bongo.util.RenderEntityCache;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.ForgeSpawnEggItem;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nullable;
import java.util.Comparator;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskTypeEntity implements TaskTypeSimple<EntityType<?>> {

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
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-2, -2, 0);
        poseStack.scale(22 / 26f, 22 / 26f, 1);
        GuiComponent.blit(poseStack, 0, 0, 0, 44, 26, 26, 256, 256);
    }

    @Override
    public void renderSlotContent(Minecraft mc, EntityType<?> content, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        @SuppressWarnings("unchecked")
        EntityRenderer<Entity> render = (EntityRenderer<Entity>) mc.getEntityRenderDispatcher().renderers.get(content);
        if (render != null) {
            Entity entity = RenderEntityCache.getRenderEntity(mc, content);
            AABB bb = entity.getBoundingBoxForCulling();
            float scale = (float) Math.min(Math.min(8d / bb.getXsize(), 16d / bb.getYsize()), 8d / bb.getZsize());
            poseStack.translate(8, 16, 50);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(45));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(2));
            entity.tickCount = ClientTickHandler.ticksInGame;
            render.render(entity, 0, 0, poseStack, buffer, LightTexture.pack(15, 15));
            if (buffer instanceof MultiBufferSource.BufferSource source) source.endBatch();
        }
    }

    @Override
    public String getTranslatedContentName(EntityType<?> content) {
        return content.getDescription().getString(18);
    }

    @Override
    public Component getContentName(EntityType<?> content, MinecraftServer server) {
        return content.getDescription();
    }

    @Override
    public boolean shouldComplete(EntityType<?> element, Player player, EntityType<?> compare) {
        return element == compare;
    }

    @Override
    public Predicate<ItemStack> bongoTooltipStack(EntityType<?> element) {
        Item item = ForgeSpawnEggItem.fromEntityType(element);
        if (item == null) {
            return stack -> false;
        } else {
            return stack -> !stack.isEmpty() && stack.getItem() == item;
        }
    }

    @Override
    public CompoundTag serializeNBT(EntityType<?> element) {
        CompoundTag nbt = new CompoundTag();
        Util.putByForgeRegistry(ForgeRegistries.ENTITIES, nbt, "entity", element);
        return nbt;
    }

    @Override
    public EntityType<?> deserializeNBT(CompoundTag nbt) {
        EntityType<?> type = Util.getFromRegistry(ForgeRegistries.ENTITIES, nbt, "entity");
        if (!type.canSummon()) {
            throw new IllegalStateException("Can't use non-summonable entity type for entity tasks: " + ForgeRegistries.ENTITIES.getKey(type));
        }
        return type;
    }

    @Nullable
    @Override
    public Comparator<EntityType<?>> getSorting() {
        return Comparator.comparing(ForgeRegistries.ENTITIES::getKey, Util.COMPARE_RESOURCE);
    }
    
    @Override
    public Stream<EntityType<?>> getAllElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return ForgeRegistries.ENTITIES.getValues().stream().filter(type -> type.canSummon() && type.getCategory() != MobCategory.MISC);
        } else {
            return player.getCommandSenderWorld().getEntities(player, new AABB(player.getX() - 10, player.getY() - 5, player.getZ() - 10, player.getX() + 10, player.getY() + 5, player.getZ() + 10)).stream().<EntityType<?>>map(Entity::getType).collect(Collectors.toSet()).stream();
        }
    }
}
