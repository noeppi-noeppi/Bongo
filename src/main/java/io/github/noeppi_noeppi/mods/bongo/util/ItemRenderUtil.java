package io.github.noeppi_noeppi.mods.bongo.util;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.render.RenderHelper;

public class ItemRenderUtil {
    
    public static void renderItem(PoseStack poseStack, MultiBufferSource buffer, ItemStack stack, boolean includeAmount) {
        RenderHelper.resetColor();
        poseStack.pushPose();
        Minecraft.getInstance().getItemRenderer().renderAndDecorateFakeItem(poseStack, stack, 0, 0);
        if (includeAmount) {
            poseStack.translate(0, 0, 20);
            Minecraft.getInstance().getItemRenderer().renderGuiItemDecorations(poseStack, Minecraft.getInstance().font, stack, 0, 0);
        }
        poseStack.popPose();
        RenderHelper.resetColor();
    }
}
