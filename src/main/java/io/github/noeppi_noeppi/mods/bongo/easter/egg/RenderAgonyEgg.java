package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import wayoftime.bloodmagic.common.item.BloodMagicItems;

import javax.annotation.Nonnull;

public class RenderAgonyEgg extends RenderEgg<TileAgonyEgg> {
    
    public RenderAgonyEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileAgonyEgg tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        matrixStack.push();
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-0.5, 0, -0.5);
        matrixStack.translate(0.25, 0.95, 0.5);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(160));
        matrixStack.scale(0.8f, 0.8f, 0.8f);
        RenderHelperItem.renderItemTinted(new ItemStack(BloodMagicItems.DAGGER_OF_SACRIFICE.get()), ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 1, 1, 1, (float) (0.8 + (Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 10) + 1) * 0.1));
        matrixStack.pop();
    }
}
