package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.mojang.blaze3d.matrix.MatrixStack;
import elucent.eidolon.Registry;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class RenderEvilEgg extends RenderEgg<TileEvilEgg> {
    
    public RenderEvilEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileEvilEgg tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        matrixStack.push();
        matrixStack.translate(0.5, 1.15, 0.5);
        matrixStack.scale(0.5f, 0.5f, 0.5f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees((ClientTickHandler.ticksInGame + partialTicks) * 1.2f));
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(Registry.UNHOLY_SYMBOL.get()), ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
        matrixStack.pop();
    }
}
