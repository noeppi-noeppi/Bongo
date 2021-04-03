package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.lothrazar.cyclic.ModCyclic;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.libx.render.RenderHelperItem;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.LazyValue;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.registries.ForgeRegistries;

import javax.annotation.Nonnull;

public class RenderMysteriousEgg extends RenderEgg<TileMysteriousEgg> {
    
    public final LazyValue<ItemStack> stack1 = new LazyValue<>(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cyclic", "lightning_scepter"))));
    public final LazyValue<ItemStack> stack2 = new LazyValue<>(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cyclic", "ice_scepter"))));
    public final LazyValue<ItemStack> stack3 = new LazyValue<>(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cyclic", "evoker_fang"))));
    public final LazyValue<ItemStack> stack4 = new LazyValue<>(() -> new ItemStack(ForgeRegistries.ITEMS.getValue(new ResourceLocation("cyclic", "charm_home"))));
    
    public RenderMysteriousEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileMysteriousEgg tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        float alpha1 = (float) (((Math.sin(0.05 * (ClientTickHandler.ticksInGame + partialTicks)) + 1) * 0.45) + 0.1);
        float alpha2 = (float) (((Math.sin(0.05 * (ClientTickHandler.ticksInGame + partialTicks + 800)) + 1) * 0.45) + 0.1);
        float alpha3 = (float) (((Math.sin(0.05 * (ClientTickHandler.ticksInGame + partialTicks + 1600)) + 1) * 0.45) + 0.1);
        float alpha4 = (float) (((Math.sin(0.05 * (ClientTickHandler.ticksInGame + partialTicks + 2400)) + 1) * 0.45) + 0.1);
        matrixStack.push();
        matrixStack.translate(0.5, 0.85, 0.6);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(35));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(10));
        RenderHelperItem.renderItemTinted(this.stack1.getValue(), ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 1, 1, 1, alpha1);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.4, 0.6, 0.3);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-60));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(10));
        RenderHelperItem.renderItemTinted(this.stack2.getValue(), ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 1, 1, 1, alpha2);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.translate(0.6, 0.6, 0.5);
        RenderHelperItem.renderItemTinted(this.stack3.getValue(), ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 1, 1, 1, alpha3);
        matrixStack.pop();
        matrixStack.push();
        matrixStack.rotate(Vector3f.YP.rotationDegrees(90));
        matrixStack.translate(-0.5, 0.4, 0.05);
        matrixStack.translate(0.2, -0.1, 0);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees((float) (20 * Math.cos(0.01 * (ClientTickHandler.ticksInGame + partialTicks)))));
        matrixStack.translate(-0.2, 0.1, 0);
        matrixStack.scale(0.7f, 0.7f, 0.7f);
        RenderHelperItem.renderItemTinted(this.stack4.getValue(), ItemCameraTransforms.TransformType.FIXED, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer, 1, 1, 1, alpha4);
        matrixStack.pop();
    }
}
