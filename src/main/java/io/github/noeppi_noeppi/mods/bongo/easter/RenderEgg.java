package io.github.noeppi_noeppi.mods.bongo.easter;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.noeppi_noeppi.libx.annotation.Model;
import io.github.noeppi_noeppi.libx.block.tesr.HorizontalRotatedTesr;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.LightTexture;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;

import javax.annotation.Nonnull;

public class RenderEgg<T extends TileEntityBase> extends HorizontalRotatedTesr<T> {

    @Model("block/white_egg")
    public static IBakedModel WHITE_EGG = null;
    
    public RenderEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull T tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        if (tile.getBlockState().get(BlockStateProperties.LIT)) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.5, 0.5);
            matrixStack.scale(0.3f, 0.3f, 0.3f);
            matrixStack.translate(-0.5, -0.5, -0.5);
            IVertexBuilder vertex = Minecraft.getInstance().getRenderTypeBuffers().getOutlineBufferSource().getBuffer(RenderType.getSolid());
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
