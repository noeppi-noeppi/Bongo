package io.github.noeppi_noeppi.mods.bongo.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;
import org.moddingx.libx.render.RenderHelper;

public class ItemRenderUtil {
    
    public static void renderItem(GuiGraphics graphics, ItemStack stack, boolean includeAmount) {
        RenderHelper.resetColor();
        graphics.pose().pushPose();
        graphics.renderFakeItem(stack, 0, 0);
        if (includeAmount) {
            graphics.pose().translate(0, 0, 20);
            graphics.renderItemDecorations(Minecraft.getInstance().font, stack, 0, 0);
        }
        graphics.pose().popPose();
        RenderHelper.resetColor();
    }
}
