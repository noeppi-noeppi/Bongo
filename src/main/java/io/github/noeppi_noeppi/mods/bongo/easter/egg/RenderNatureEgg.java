package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Vector3f;
import vazkii.botania.common.item.ModItems;

import javax.annotation.Nonnull;
import java.util.List;

public class RenderNatureEgg extends RenderEgg<TileEntityBase> {

    public static List<ItemStack> ITEMS = ImmutableList.of(
            new ItemStack(ModItems.runeWater),
            new ItemStack(ModItems.runeSpring),
            new ItemStack(ModItems.runeFire),
            new ItemStack(ModItems.runeSummer),
            new ItemStack(ModItems.runeEarth),
            new ItemStack(ModItems.runeAutumn),
            new ItemStack(ModItems.runeAir),
            new ItemStack(ModItems.runeWinter)
    );

    public RenderNatureEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileEntityBase tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        float angle = 360f / ITEMS.size();
        float time = ClientTickHandler.ticksInGame + partialTicks;
        for (int i = 0; i < ITEMS.size(); i++) {
            matrixStack.push();
            matrixStack.translate(0.5, 0.75, 0.5);
            matrixStack.scale(0.3f, 0.3f, 0.3f);
            matrixStack.rotate(Vector3f.YP.rotationDegrees((angle * i) + time));
            matrixStack.translate(1.5, 0, 0.25);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(90f));
            matrixStack.translate(0, 0.075 * Math.sin((time + (i * 10)) / 5d), 0);
            matrixStack.scale(1.2f, 1.2f, 1.2f);
            Minecraft.getInstance().getItemRenderer().renderItem(ITEMS.get(i), ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            matrixStack.pop();
        }
    }
}
