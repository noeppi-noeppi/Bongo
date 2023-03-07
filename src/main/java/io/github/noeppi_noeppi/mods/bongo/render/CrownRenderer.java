package io.github.noeppi_noeppi.mods.bongo.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import org.joml.Matrix3f;
import org.joml.Matrix4f;

import javax.annotation.Nonnull;
import java.util.Map;

@SuppressWarnings("SameParameterValue")
public class CrownRenderer extends RenderLayer<Player, EntityModel<Player>> {

    public static final ResourceLocation CROWN_TEXTURE = new ResourceLocation(BongoMod.getInstance().modid, "textures/player/crown.png");
    public static final RenderType CROWN_TYPE = RenderType.entityCutout(CROWN_TEXTURE);

    public CrownRenderer(RenderLayerParent<Player, EntityModel<Player>> entityRenderer) {
        super(entityRenderer);
    }

    @Override
    public void render(@Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, @Nonnull Player player, float limbSwing, float limbSwingAmount, float partialTicks, float ticksExisted, float headYaw, float headPitch) {
        if (!player.isInvisible()) {
            Bongo bongo = Bongo.get(player.getCommandSenderWorld());
            if (bongo.active() && bongo.won() && bongo.winningTeam().hasPlayer(player)) {
                poseStack.pushPose();
                if (player.isShiftKeyDown() && !player.getAbilities().flying) {
                    poseStack.translate(0, 0.25, 0);
                }
                poseStack.mulPose(Axis.YP.rotationDegrees(headYaw));
                if (!player.isFallFlying() && !player.isSwimming()) {
                    poseStack.mulPose(Axis.XP.rotationDegrees(headPitch));
                } else {
                    poseStack.mulPose(Axis.XP.rotationDegrees(-40));
                }
                poseStack.translate(-0.5, -0.9d, 0);
                poseStack.scale(0.6f, 0.6f, 0.6f);
                poseStack.translate(0.4, -0.2, 0);

                poseStack.pushPose();
                poseStack.translate(0, 0, -0.425);
                renderSprites(poseStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                poseStack.translate(0, 0, 0.85);
                renderSprites(poseStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();

                poseStack.pushPose();
                poseStack.mulPose(Axis.YP.rotationDegrees(90));
                poseStack.translate(-0.425, 0, 0);
                renderSprites(poseStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                poseStack.translate(0, 0, 0.85);
                renderSprites(poseStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                poseStack.popPose();

                poseStack.popPose();
            }
        }
    }

    @SuppressWarnings("SameParameterValue")
    private void renderSprites(PoseStack poseStack, MultiBufferSource buffer, double width, double height, float alpha, int overlay) {
        poseStack.pushPose();
        renderSprite(poseStack, buffer, 0, 0, width, height, alpha, overlay);
        poseStack.translate(width / 2d, 0, 0);
        poseStack.mulPose(Axis.YP.rotationDegrees(180));
        poseStack.translate(width / -2d, 0, 0);
        renderSprite(poseStack, buffer, 0, 0, width, height, alpha, overlay);
        poseStack.popPose();
    }

    @SuppressWarnings("SameParameterValue")
    private void renderSprite(PoseStack poseStack, MultiBufferSource buffer, double x, double y, double width, double height, float alpha, int overlay) {
        VertexConsumer vertex = buffer.getBuffer(CROWN_TYPE);
        Matrix4f model = poseStack.last().pose();
        Matrix3f normal = poseStack.last().normal();
        int light = LightTexture.pack(15, 15);
        vertex.vertex(model, (float) x, (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 1).overlayCoords(overlay).uv2(light).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.vertex(model, (float) (x + width), (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 1).overlayCoords(overlay).uv2(light).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.vertex(model, (float) (x + width), (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(1, 0).overlayCoords(overlay).uv2(light).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.vertex(model, (float) x, (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).uv(0, 0).overlayCoords(overlay).uv2(light).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register() {
        try {
            Map<String, EntityRenderer<? extends Player>> skinMap = Minecraft.getInstance().getEntityRenderDispatcher().getSkinMap();
            if (skinMap.get("default") instanceof LivingEntityRenderer render) render.addLayer(new CrownRenderer((RenderLayerParent<Player, EntityModel<Player>>) render));
            if (skinMap.get("slim") instanceof LivingEntityRenderer render) render.addLayer(new CrownRenderer((RenderLayerParent<Player, EntityModel<Player>>) render));
        } catch (Exception e) {
            // Just in case
            e.printStackTrace();
        }
    }
}
