package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.feywild.feywild.entity.ModEntityTypes;
import com.feywild.feywild.entity.SpringPixieEntity;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class RenderDreamingEgg extends RenderEgg<TileDreamingEgg> {
    
    public RenderDreamingEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileDreamingEgg tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        if (tile.getWorld() != null) {
            SpringPixieEntity entity = ModEntityTypes.springPixie.create(tile.getWorld());
            if (entity != null) {
                entity.ticksExisted = ClientTickHandler.ticksInGame;
                EntityRenderer<? super SpringPixieEntity> renderer = Minecraft.getInstance().getRenderManager().getRenderer(entity);
                //noinspection ConstantConditions
                if (renderer != null) {
                    matrixStack.push();
                    matrixStack.translate(0.5, 0.45, 0.5);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(ClientTickHandler.ticksInGame + partialTicks));
                    matrixStack.translate(0.52, 0, 0);
                    matrixStack.rotate(Vector3f.YP.rotationDegrees(180));
                    matrixStack.translate(0, 0.02 * Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 5), 0);
                    matrixStack.scale(0.5f, 0.5f, 0.5f);
                    renderer.render(entity, 0, partialTicks, matrixStack, buffer, light);
                    matrixStack.pop();
                }
            }
        }
    }
}
