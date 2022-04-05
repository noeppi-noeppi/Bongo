package io.github.noeppi_noeppi.mods.bongo.easter;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.BlockTE;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.mods.bongo.Bongo;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.ModBlocks;
import io.github.noeppi_noeppi.mods.bongo.easter.egg.RenderNatureEgg;
import io.github.noeppi_noeppi.mods.bongo.task.TaskTypeEgg;
import net.minecraft.block.*;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;

public class BlockEgg<T extends TileEntityBase> extends BlockTE<T> implements IWaterLoggable {

    private static final VoxelShape SHAPE = Block.makeCuboidShape(1.0D, 0.0D, 1.0D, 15.0D, 16.0D, 15.0D);
    
    public BlockEgg(ModX mod, Class<T> clazz, Properties properties) {
        this(mod, clazz, properties, new Item.Properties());
    }

    public BlockEgg(ModX mod, Class<T> clazz, Properties properties, Item.Properties itemProperties) {
        super(mod, clazz, properties.hardnessAndResistance(-1.0F, 3600000.0F), itemProperties);
        setDefaultState(getStateContainer().getBaseState().with(BlockStateProperties.HORIZONTAL_FACING, Direction.SOUTH)
                .with(BlockStateProperties.WATERLOGGED, false)
                .with(BlockStateProperties.LIT, false));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id) {
        registerTESR();
    }
    
    @OnlyIn(Dist.CLIENT)
    public void registerTESR() {
        ClientRegistry.bindTileEntityRenderer(getTileType(), RenderEgg::new);
    }

    @Nullable
    @Override
    public BlockState getStateForPlacement(BlockItemUseContext ctx) {
        return this.getDefaultState()
                .with(BlockStateProperties.HORIZONTAL_FACING, ctx.getPlacementHorizontalFacing().getOpposite())
                .with(BlockStateProperties.WATERLOGGED, ctx.getWorld().getFluidState(ctx.getPos()).getFluid() == Fluids.WATER)
                .with(BlockStateProperties.LIT, false);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(BlockStateProperties.HORIZONTAL_FACING)
                .add(BlockStateProperties.WATERLOGGED)
                .add(BlockStateProperties.LIT);
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.get(BlockStateProperties.WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public void addInformation(@Nonnull ItemStack stack, @Nullable IBlockReader world, @Nonnull List<ITextComponent> tooltip, @Nonnull ITooltipFlag tooltipFlag) {
        super.addInformation(stack, world, tooltip, tooltipFlag);
        Objects.requireNonNull(getRegistryName());
        tooltip.add(new TranslationTextComponent("description." + this.getRegistryName().getNamespace() + "." + this.getRegistryName().getPath()).mergeStyle(TextFormatting.GREEN));
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public VoxelShape getShape(@Nonnull BlockState state, @Nonnull IBlockReader world, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
        return SHAPE;
    }

    @SuppressWarnings("deprecation")
    @Nonnull
    @Override
    public ActionResultType onBlockActivated(@Nonnull BlockState state, @Nonnull World world, @Nonnull BlockPos pos, @Nonnull PlayerEntity player, @Nonnull Hand hand, @Nonnull BlockRayTraceResult hit) {
        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem() == ModBlocks.basket.asItem()) {
            if (world.isRemote) return ActionResultType.SUCCESS;
            CompoundNBT blockNBT = stack.getOrCreateChildTag("BlockEntityTag");
            ListNBT list;
            if (blockNBT.contains("items", Constants.NBT.TAG_LIST)) {
                list = blockNBT.getList("items", Constants.NBT.TAG_COMPOUND);
            } else {
                list = new ListNBT();
            }
            list.add(new ItemStack(this).write(new CompoundNBT()));
            blockNBT.put("items", list);
            world.setBlockState(pos, state.getFluidState().getBlockState(), 3);
            Bongo bongo = Bongo.get(world);
            bongo.checkCompleted(TaskTypeEgg.INSTANCE, player, this);
            player.addStat(BongoMod.getInstance().STAT_EGGS);
            if (player instanceof ServerPlayerEntity) {
                ((ServerPlayerEntity) player).getServerWorld().getServer().getPlayerList().getPlayerStats(player).markAllDirty();
                ((ServerPlayerEntity) player).getServerWorld().getServer().getPlayerList().getPlayerStats(player).sendStats((ServerPlayerEntity) player);
            }
            return ActionResultType.CONSUME;
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }
}
