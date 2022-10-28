package io.github.noeppi_noeppi.mods.bongo.task;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import com.mojang.serialization.MapCodec;
import com.natamus.pumpkillagersquest.api.PumpkillagerSummonEvent;
import com.natamus.pumpkillagersquest.util.SpookyHeads;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.moddingx.libx.codec.MoreCodecs;
import org.moddingx.libx.render.ClientTickHandler;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Objects;
import java.util.stream.Stream;

public class TaskTypePumpkillager implements TaskType<TaskTypePumpkillager.Type> {

    public static final TaskTypePumpkillager INSTANCE = new TaskTypePumpkillager();
    
    private Villager renderVillager = null;
    
    private TaskTypePumpkillager() {
        
    }
    
    @Override
    public String id() {
        return "spooky.pumpkillager";
    }

    @Override
    public Class<Type> taskClass() {
        return Type.class;
    }

    @Override
    public MapCodec<Type> codec() {
        return MoreCodecs.enumCodec(Type.class).fieldOf("value");
    }

    @Override
    public Component name() {
        return Component.translatable("bongo.spooky.pumpkillager.task");
    }

    @Override
    public Component contentName(Type element, @Nullable MinecraftServer server) {
        return element.name;
    }

    @Override
    public Comparator<Type> order() {
        return Comparator.comparing(Type::ordinal);
    }

    @Override
    public Stream<Type> listElements(MinecraftServer server, @Nullable ServerPlayer player) {
        return Arrays.stream(Type.values());
    }

    @Override
    public boolean shouldComplete(ServerPlayer player, Type element, Type compare) {
        return element == compare;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlot(Minecraft mc, PoseStack poseStack, MultiBufferSource buffer) {
        poseStack.translate(-2, -2, 0);
        poseStack.scale(22 / 26f, 22 / 26f, 1);
        GuiComponent.blit(poseStack, 0, 0, 0, 44, 26, 26, 256, 256);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void renderSlotContent(Minecraft mc, Type element, PoseStack poseStack, MultiBufferSource buffer, boolean bigBongo) {
        @SuppressWarnings("unchecked")
        EntityRenderer<Entity> render = (EntityRenderer<Entity>) mc.getEntityRenderDispatcher().renderers.get(EntityType.VILLAGER);
        if (render != null) {
            if (renderVillager == null || renderVillager.level != mc.level) {
                renderVillager = new Villager(EntityType.VILLAGER, Objects.requireNonNull(mc.level));
                renderVillager.getVillagerData().setType(VillagerType.SNOW);
            }
            switch (element) {
                case INITIAL_ENCOUNTER -> renderVillager.setItemSlot(EquipmentSlot.HEAD, SpookyHeads.getCarvedPumpkin(1));
                case AFTER_RITUAL -> renderVillager.setItemSlot(EquipmentSlot.HEAD, SpookyHeads.getJackoLantern(1));
                case FINAL_BOSS -> renderVillager.setItemSlot(EquipmentSlot.HEAD, SpookyHeads.getEvilJackoLantern(1));
            }
            AABB bb = renderVillager.getBoundingBoxForCulling();
            float scale = (float) Math.min(Math.min(8d / bb.getXsize(), 16d / bb.getYsize()), 8d / bb.getZsize());
            poseStack.translate(8, 16, 50);
            poseStack.scale(scale, scale, scale);
            poseStack.mulPose(Vector3f.ZP.rotationDegrees(180));
            poseStack.mulPose(Vector3f.YP.rotationDegrees(45));
            poseStack.mulPose(Vector3f.XP.rotationDegrees(2));
            renderVillager.tickCount = ClientTickHandler.ticksInGame;
            render.render(renderVillager, 0, 0, poseStack, buffer, LightTexture.pack(15, 15));
            if (buffer instanceof MultiBufferSource.BufferSource source) source.endBatch();
        }
    }

    public enum Type {
        INITIAL_ENCOUNTER(Component.translatable("bongo.spooky.pumpkillager.initial")),
        AFTER_RITUAL(Component.translatable("bongo.spooky.pumpkillager.ritual")),
        FINAL_BOSS(Component.translatable("bongo.spooky.pumpkillager.boss"));

        private final Component name;
        
        Type(Component name) {
            this.name = name;
        }
        
        @Nullable
        public static Type of(PumpkillagerSummonEvent.Type type) {
            return switch (type) {
                case INITIAL_SUMMON -> INITIAL_ENCOUNTER;
                case POST_RITUAL -> AFTER_RITUAL;
                case FINAL_BOSS -> FINAL_BOSS;
                //noinspection UnnecessaryDefault
                default -> null;
            };
        }
    }
}
