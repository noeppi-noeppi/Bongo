package io.github.noeppi_noeppi.mods.bongo.easter;

import io.github.noeppi_noeppi.libx.block.DirectionShape;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockTE;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.IWaterLoggable;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.state.properties.SlabType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBasket extends BlockTE<TileBasket> {

    private static final DirectionShape shape = new DirectionShape(VoxelShapes.or(
            makeCuboidShape(2, 0, 2, 14, 1, 14),
            makeCuboidShape(1, 1, 2, 2, 3.5, 14),
            makeCuboidShape(2, 1, 1, 14, 3.5, 2),
            makeCuboidShape(14, 1, 2, 15, 3.5, 14),
            makeCuboidShape(2, 1, 14, 14, 3.5, 15),
            makeCuboidShape(0, 3.5, 1, 1, 7, 15),
            makeCuboidShape(1, 3.5, 0, 15, 7, 1),
            makeCuboidShape(15, 3.5, 1, 16, 7, 15),
            makeCuboidShape(1, 3.5, 15, 15, 7, 16),
            makeCuboidShape(1, 3, 1, 2, 4, 2),
            makeCuboidShape(1, 3, 14, 2, 4, 15),
            makeCuboidShape(14, 3, 1, 15, 4, 2),
            makeCuboidShape(14, 3, 14, 15, 4, 15),
            makeCuboidShape(6.92, 6.92, -0.08, 9.08, 12.08, 1.08),
            makeCuboidShape(6.92, 6.92, 14.92, 9.08, 12.08, 16.08),
            makeCuboidShape(7, 15.33921, 4.23092, 9, 16.33921, 11.73092)
    ));

    public BlockBasket(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockBasket(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileBasket.class, properties, itemProperties.setISTER(() -> ItemStackRenderer::get));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id) {
        RenderTypeLookup.setRenderLayer(this, RenderType.getCutout());
        ClientRegistry.bindTileEntityRenderer(this.getTileType(), RenderBasket::new);
        ItemStackRenderer.addRenderTile(this.getTileType(), true);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(BlockStateProperties.HORIZONTAL_FACING, context.getPlacementHorizontalFacing().rotateY());
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public VoxelShape getShape(BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return shape.getShape(state.get(BlockStateProperties.HORIZONTAL_FACING));
    }
}
