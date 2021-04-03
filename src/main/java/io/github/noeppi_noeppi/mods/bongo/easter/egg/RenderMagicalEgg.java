package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.noeppi_noeppi.libx.annotation.Model;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class RenderMagicalEgg extends RenderEgg<TileMagicalEgg> {

    @Model("block/magical_egg")
    public static IBakedModel MODEL = null;
    
    public RenderMagicalEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileMagicalEgg tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        matrixStack.push();
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.rotate(Vector3f.YP.rotationDegrees((tile.getPos().getX() % 2 == 0 ? -1 : 1) * 1.8f * (ClientTickHandler.ticksInGame + partialTicks)));
        matrixStack.scale(0.8f, 0.8f, 0.8f);
        matrixStack.translate(-0.5, 0.15 + 0.15 * Math.sin(0.043 * (ClientTickHandler.ticksInGame + partialTicks + (tile.getPos().getZ() / 37d))), -0.5);
        IVertexBuilder vertex = buffer.getBuffer(RenderTypeLookup.func_239220_a_(tile.getBlockState(), false));
        //noinspection deprecation
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer()
                .renderModelBrightnessColor(matrixStack.getLast(), vertex, tile.getBlockState(),
                        MODEL, 1, 1, 1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.pop();
    }
}
