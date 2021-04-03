package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import com.simibubi.create.AllBlockPartials;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.content.contraptions.base.DirectionalAxisKineticBlock;
import com.simibubi.create.foundation.utility.SuperByteBuffer;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import io.github.noeppi_noeppi.mods.bongo.easter.RenderEgg;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.vector.Vector3f;

import javax.annotation.Nonnull;

public class RenderMechanicalEgg extends RenderEgg<TileEntityBase> {

    public RenderMechanicalEgg(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    protected void doRender(@Nonnull TileEntityBase tile, float partialTicks, @Nonnull MatrixStack matrixStack, @Nonnull IRenderTypeBuffer buffer, int light, int overlay) {
        super.doRender(tile, partialTicks, matrixStack, buffer, light, overlay);
        BlockState deployerState = AllBlocks.DEPLOYER.getDefaultState().with(BlockStateProperties.FACING, Direction.UP).with(DirectionalAxisKineticBlock.AXIS_ALONG_FIRST_COORDINATE, true);
        SuperByteBuffer pole = AllBlockPartials.DEPLOYER_POLE.renderOn(deployerState);
        SuperByteBuffer hand = AllBlockPartials.DEPLOYER_HAND_POINTING.renderOn(deployerState);
        matrixStack.push();
        matrixStack.translate(0, 0.4, 0.5);
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-50));
        matrixStack.translate(0, 0, 0.1 * Math.sin((ClientTickHandler.ticksInGame + partialTicks) / 5));
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.scale(0.3f, 0.3f, 0.3f);
        matrixStack.translate(-0.5, 0, -0.5);
        IVertexBuilder vertex = buffer.getBuffer(RenderType.getSolid());
        pole.renderInto(matrixStack, vertex);
        hand.renderInto(matrixStack, vertex);
        matrixStack.pop();
        BlockState cogWheelState = AllBlocks.COGWHEEL.getDefaultState().with(BlockStateProperties.AXIS, Direction.Axis.Y);
        SuperByteBuffer cogwheel = AllBlockPartials.SHAFTLESS_COGWHEEL.renderOn(cogWheelState);
        matrixStack.push();
        matrixStack.translate(0.2, 0.35, -0.2);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-25));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(-45));
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.scale(0.4f, 0.4f, 0.4f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(5 * ClientTickHandler.ticksInGame + partialTicks));
        matrixStack.translate(-0.5, 0, -0.5);
        vertex = buffer.getBuffer(RenderType.getSolid());
        cogwheel.renderInto(matrixStack, vertex);
        matrixStack.pop();
        cogwheel = AllBlockPartials.SHAFTLESS_COGWHEEL.renderOn(cogWheelState);
        matrixStack.push();
        matrixStack.translate(1.5, 0.2, 0.25);
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(90));
        matrixStack.rotate(Vector3f.XP.rotationDegrees(-10));
        matrixStack.rotate(Vector3f.ZP.rotationDegrees(20));
        matrixStack.translate(0.5, 0, 0.5);
        matrixStack.scale(0.6f, 0.6f, 0.6f);
        matrixStack.rotate(Vector3f.YP.rotationDegrees(-1.2f * ClientTickHandler.ticksInGame + partialTicks));
        matrixStack.translate(-0.5, 0, -0.5);
        vertex = buffer.getBuffer(RenderType.getSolid());
        cogwheel.renderInto(matrixStack, vertex);
        matrixStack.pop();
    }
}
