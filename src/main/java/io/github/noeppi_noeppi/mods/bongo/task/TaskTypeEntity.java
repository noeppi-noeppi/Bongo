package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.math.Axis;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.util.RenderEntityCache;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;
import org.moddingx.libx.render.ClientTickHandler;

import javax.annotation.Nullable;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TaskTypeEntity extends RegistryTaskType<EntityType<?>> {

    public static final TaskTypeEntity INSTANCE = new TaskTypeEntity();

    private TaskTypeEntity() {
        //noinspection unchecked
        super((Class<EntityType<?>>) (Class<?>) EntityType.class, ForgeRegistries.ENTITY_TYPES);
    }

    @Override
    public String id() {
        return "bongo.entity";
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.task.entity.name");
    }

    @Override
    public Component contentName(EntityType<?> element, @Nullable MinecraftServer server) {
        return element.getDescription();
    }

    @Override
    public Stream<EntityType<?>> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        if (player == null) {
            return ForgeRegistries.ENTITY_TYPES.getValues().stream().filter(type -> type.canSummon() && type.getCategory() != MobCategory.MISC);
        } else {
            return player.getCommandSenderWorld().getEntities(player, new AABB(player.getX() - 10, player.getY() - 5, player.getZ() - 10, player.getX() + 10, player.getY() + 5, player.getZ() + 10)).stream().<EntityType<?>>map(Entity::getType).collect(Collectors.toSet()).stream();
        }
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, GuiGraphics graphics) {
        graphics.pose().translate(-2, -2, 0);
        graphics.pose().scale(22 / 26f, 22 / 26f, 1);
        graphics.blit(RenderOverlay.BINGO_SLOTS_TEXTURE, 0, 0, 0, 44, 26, 26, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, GuiGraphics graphics, EntityType<?> element, boolean bigBongo) {
        @SuppressWarnings("unchecked")
        EntityRenderer<Entity> render = (EntityRenderer<Entity>) mc.getEntityRenderDispatcher().renderers.get(element);
        if (render != null) {
            Entity entity = RenderEntityCache.getRenderEntity(mc, element);
            AABB bb = entity.getBoundingBoxForCulling();
            float scale = (float) Math.min(Math.min(8d / bb.getXsize(), 16d / bb.getYsize()), 8d / bb.getZsize());
            graphics.pose().translate(8, 16, 50);
            graphics.pose().scale(scale, scale, scale);
            graphics.pose().mulPose(Axis.ZP.rotationDegrees(180));
            graphics.pose().mulPose(Axis.YP.rotationDegrees(45));
            graphics.pose().mulPose(Axis.XP.rotationDegrees(2));
            entity.tickCount = ClientTickHandler.ticksInGame();
            render.render(entity, 0, 0, graphics.pose(), graphics.bufferSource(), LightTexture.pack(15, 15));
            graphics.bufferSource().endBatch();
        }
    }
}
