package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import org.moddingx.libx.render.RenderHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;

public class ItemRenderUtil {
    
    public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, ItemStack stack, boolean includeAmount) {
        RenderHelper.resetColor();
        RenderSystem.getModelViewStack().pushPose();
        RenderSystem.getModelViewStack().mulPoseMatrix(poseStack.last().pose());
        RenderSystem.applyModelViewMatrix();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(stack, 0, 0);
        if (includeAmount) {
            RenderSystem.getModelViewStack().translate(0, 0, 20);
            RenderSystem.applyModelViewMatrix();
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(Minecraft.getInstance().font, stack, 0, 0);
        }
        RenderSystem.getModelViewStack().popPose();
        RenderHelper.resetColor();
        RenderSystem.applyModelViewMatrix();
    }
}
