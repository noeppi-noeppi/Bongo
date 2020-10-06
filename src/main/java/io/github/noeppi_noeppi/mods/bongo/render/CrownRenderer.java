package io.github.noeppi_noeppi.mods.bongo.render;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Matrix3f;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;
import java.util.Map;

public class CrownRenderer extends LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    public static final ResourceLocation CROWN_TEXTURE = new ResourceLocation(BongoMod.MODID, "textures/player/crown.png");
    public static final RenderType CROWN_TYPE = RenderType.getEntityCutout(CROWN_TEXTURE);

    public CrownRenderer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> entityRendererIn) {
        super(entityRendererIn);
    }

    @Override
    public void render(@Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, @Nonnull AbstractClientPlayerEntity player, float swing, float swingAmount, float partialTicks, float ageTicks, float yaw, float pitch) {
        if (!player.isInvisible()) {
            Bongo bongo = Bongo.get(player.getEntityWorld());
            if (bongo.active() && bongo.won() && bongo.winningTeam().hasPlayer(player)) {
                matrixStack.push();
                if (player.isSneaking()) {
                    matrixStack.translate(0, 0.25, 0);
                }
                matrixStack.rotate(Vector3f.YP.rotationDegrees(yaw));
                if (!player.isElytraFlying() && !player.isSwimming()) {
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(pitch));
                } else {
                    matrixStack.rotate(Vector3f.XP.rotationDegrees(-40));
                }
                matrixStack.translate(-0.5, -0.9d, 0);
                matrixStack.scale(0.6f, 0.6f, 0.6f);
                matrixStack.translate(0.4, -0.2, 0);

                matrixStack.push();
                matrixStack.translate(0, 0, -0.425);
                renderSprites(matrixStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                matrixStack.translate(0, 0, 0.85);
                renderSprites(matrixStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                matrixStack.pop();

                matrixStack.push();
                matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
                matrixStack.translate(-0.425, 0, 0);
                renderSprites(matrixStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                matrixStack.translate(0, 0, 0.85);
                renderSprites(matrixStack, buffer, 0.85, 0.85, 1, OverlayTexture.NO_OVERLAY);
                matrixStack.pop();

                matrixStack.pop();
            }
        }
    }

    private void renderSprites(MatrixStack matrixStack, IRenderTypeBuffer buffer, double width, double height, float alpha, int overlay) {
        matrixStack.push();
        renderSprite(matrixStack, buffer, 0, 0, width, height, alpha, overlay);
        matrixStack.translate(width / 2d, 0, 0);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
        matrixStack.translate(width / -2d, 0, 0);
        renderSprite(matrixStack, buffer, 0, 0, width, height, alpha, overlay);
        matrixStack.pop();
    }

    private void renderSprite(MatrixStack matrixStack, IRenderTypeBuffer buffer, double x, double y, double width, double height, float alpha, int overlay) {
        IVertexBuilder vertex = buffer.getBuffer(CROWN_TYPE);
        Matrix4f model = matrixStack.getLast().getMatrix();
        Matrix3f normal = matrixStack.getLast().getNormal();
        vertex.pos(model, (float) x, (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 1).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.pos(model, (float) (x + width), (float) (y + height), 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 1).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.pos(model, (float) (x + width), (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(1, 0).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
        vertex.pos(model, (float) x, (float) y, 0.0F).color(1.0F, 1.0F, 1.0F, alpha).tex(0, 0).overlay(overlay).lightmap(15728880).normal(normal, 1.0F, 0.0F, 0.0F).endVertex();
    }

    public static void register() {
        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
        PlayerRenderer render;
        render = skinMap.get("default");
        render.addLayer(new CrownRenderer(render));

        render = skinMap.get("slim");
        render.addLayer(new CrownRenderer(render));
    }
}
