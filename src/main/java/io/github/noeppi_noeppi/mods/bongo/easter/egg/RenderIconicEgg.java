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
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.psi.common.item.base.ModItems;

import javax.annotation.Nonnull;

public class RenderIconicEgg extends RenderEgg<TileIconicEgg> {

    @Model("block/iconic_egg")
    public static IBakedModel MODEL = null;
    
    public RenderIconicEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileIconicEgg tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);

        float scale = (float) (1 - ((Math.cos((ClientTickHandler.ticksInGame + partialTicks) / 40) + 1) / 76d));
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.scale(scale, scale, scale);
        matrixStack.translate(-0.5, -0.5, -0.5);
        
        matrixStack.push();
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.rotate(Vector3f.YP.rotationDegrees((tile.getPos().getX() % 2 == 0 ? -1 : 1) * 0.2f * (ClientTickHandler.ticksInGame + partialTicks)));
        matrixStack.scale(0.9f, 0.9f, 0.9f);
        matrixStack.translate(-0.5, 0.05, -0.5);
        IVertexBuilder vertex = buffer.getBuffer(RenderTypeLookup.func_239220_a_(tile.getBlockState(), false));
        //noinspection deprecation
        Minecraft.getInstance().getBlockRendererDispatcher().getBlockModelRenderer()
                .renderModelBrightnessColor(matrixStack.getLast(), vertex, tile.getBlockState(),
                        MODEL, 1, 1, 1, light, OverlayTexture.NO_OVERLAY);
        matrixStack.pop();
        
        ItemStack black = new ItemStack(ModItems.ebonySubstance);
        ItemStack white = new ItemStack(ModItems.ivorySubstance);
        matrixStack.push();
        matrixStack.translate(0.5, 0.5, 0.5);
        matrixStack.rotate(Vector3f.YP.rotationDegrees((tile.getPos().getX() % 2 == 0 ? 1 : -1) * ClientTickHandler.ticksInGame));
        for (int i = 0; i < 4; i++) {
            matrixStack.push();
            matrixStack.rotate(Vector3f.YP.rotationDegrees(90 * i));
            matrixStack.translate(0.55, Math.sin((ClientTickHandler.ticksInGame + (17.431 * i)) / 10f) * 0.02, 0);
            matrixStack.scale(0.8f, 0.8f, 0.8f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
            Minecraft.getInstance().getItemRenderer().renderItem(i % 2 == 0 ? white : black, ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            matrixStack.pop();
        }
        matrixStack.pop();
        
        matrixStack.pop();
    }
}
