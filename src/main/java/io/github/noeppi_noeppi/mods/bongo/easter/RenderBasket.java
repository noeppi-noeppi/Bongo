package io.github.noeppi_noeppi.mods.bongo.easter;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.block.tesr.HorizontalRotatedTesr;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.vector.Quaternion;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;
import java.util.List;

public class RenderBasket extends HorizontalRotatedTesr<TileBasket> {

    public RenderBasket(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileBasket tile, float v, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        List<ItemStack> eggs = tile.getRenderView();
        for (int i = 0; i < eggs.size(); i++) {
            matrixStack.push();
            if (transform(matrixStack, i, eggs.size())) {
                matrixStack.scale(1.4f, 1.4f, 1.4f);
                Minecraft.getInstance().getItemRenderer().renderItem(eggs.get(i), ItemCameraTransforms.TransformType.GROUND, light, OverlayTexture.NO_OVERLAY, matrixStack, buffer);
            }
            matrixStack.pop();
        }
    }

    private boolean transform(MatrixStack matrixStack, int egg, int size) {
        matrixStack.translate(0, -1 / 32d, 0);
        if (size == 1 && egg == 0) {
            matrixStack.translate(0.5, 0, 0.5);
            matrixStack.rotate(Vector3f.YP.rotationDegrees(45));
            return true;
        } else if (size == 2) {
            if (egg == 0) {
                matrixStack.translate(0.52, 0, 0.55);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(30));
                return true;
            } else if (egg == 1) {
                matrixStack.translate(0.48, 0, 0.45);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-20));
                return true;
            }
        } else if (size == 3) {
            if (egg == 0) {
                matrixStack.translate(0.52, 0, 0.55);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(30));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(20));
                return true;
            } else if (egg == 1) {
                matrixStack.translate(0.46, 0, 0.45);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-20));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(10));
                return true;
            } else if (egg == 2) {
                matrixStack.translate(0.52, 0.1, 0.52);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-30));
                return true;
            }
        } else if (size == 4) {
            if (egg == 0) {
                matrixStack.translate(0.43, 0, 0.62);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(30));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(20));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(-5));
                return true;
            } else if (egg == 1) {
                matrixStack.translate(0.47, 0, 0.38);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-20));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(10));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(10));
                return true;
            } else if (egg == 2) {
                matrixStack.translate(0.64, 0.08, 0.52);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-30));
                matrixStack.rotate(Vector3f.YP.rotationDegrees(10));
                return true;
            } else if (egg == 3) {
                matrixStack.translate(0.5, 0.16, 0.5);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(25));
                return true;
            }
        } else if (size >= 5 && size <= 16) {
            if (egg == 0) {
                matrixStack.translate(0.32, 0, 0.5);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(15));
                return true;
            } else if (egg == 1) {
                matrixStack.translate(0.4, 0, 0.35);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-20));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(10));
                return true;
            } else if (egg == 2) {
                matrixStack.translate(0.6, 0, 0.35);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-20));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-10));
                return true;
            } else if (egg == 3) {
                matrixStack.translate(0.68, 0, 0.5);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-15));
                return true;
            } else if (egg == 4) {
                matrixStack.translate(0.6, 0, 0.65);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(20));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-10));
                return true;
            } else if (egg == 5) {
                matrixStack.translate(0.4, 0, 0.65);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(20));
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(10));
                return true;
            } else if (egg == 6) {
                matrixStack.translate(0.5, 0.15, 0.5);
                return true;
            } else if (egg == 7) {
                matrixStack.translate(0.15, 0.2, 0.15);
                matrixStack.rotate(new Quaternion(new Vector3f(1, 0, -1), 20, true));
                return true;
            } else if (egg == 8) {
                matrixStack.translate(0.85, 0.2, 0.85);
                matrixStack.rotate(new Quaternion(new Vector3f(-1, 0, 1), 20, true));
                return true;
            } else if (egg == 9) {
                matrixStack.translate(0.15, 0.2, 0.85);
                matrixStack.rotate(new Quaternion(new Vector3f(-1, 0, -1), 20, true));
                return true;
            } else if (egg == 10) {
                matrixStack.translate(0.85, 0.2, 0.15);
                matrixStack.rotate(new Quaternion(new Vector3f(1, 0, 1), 20, true));
                return true;
            } else if (egg == 11) {
                matrixStack.translate(0.5, 0.27, 0.19);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(15));
                return true;
            } else if (egg == 12) {
                matrixStack.translate(0.5, 0.27, 0.81);
                matrixStack.rotate(Vector3f.XP.rotationDegrees(-15));
                return true;
            } else if (egg == 13) {
                matrixStack.translate(0.81, 0.27, 0.5);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(15));
                return true;
            } else if (egg == 14) {
                matrixStack.translate(0.19, 0.27, 0.5);
                matrixStack.rotate(Vector3f.ZP.rotationDegrees(-15));
                return true;
            } else if (egg == 15) {
                matrixStack.translate(0.5, 0.38, 0.5);
                matrixStack.rotate(Vector3f.YP.rotationDegrees(45));
                return true;
            }
        }
        return false;
    }
}
