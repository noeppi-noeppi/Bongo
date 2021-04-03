package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.noeppi_noeppi.libx.annotation.Model;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;

import javax.annotation.Nonnull;

public class RenderEnergizedEgg extends RenderEgg<TileEnergizedEgg> {

    @Model("block/white_egg")
    public static IBakedModel WHITE_EGG = null;
    
    public RenderEnergizedEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileEnergizedEgg tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        if (Minecraft.getInstance().world != null && (Minecraft.getInstance().world.getGameTime() + tile.getPos().getX() - (2l * tile.getPos().getZ())) % 60 < 4) {
            matrixStack.push();
            matrixStack.translate(-0.05, -0.05, -0.05);
            matrixStack.scale(1.1f, 1.1f, 1.1f);
            IVertexBuilder vertex = buffer.getBuffer(RenderType.getSolid());
            //noinspection deprecation
            Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer()
                    .renderModelBrightnessColor(matrixStack.getLast(), vertex, tile.getBlockState(),
                            WHITE_EGG, 1, 1, 1,
                            LightTexture.packLight(15, 15),
                            OverlayTexture.NO_OVERLAY);
            matrixStack.pop();
        }
    }
}
