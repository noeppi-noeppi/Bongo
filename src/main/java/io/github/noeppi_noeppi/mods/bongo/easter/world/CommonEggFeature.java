package io.github.noeppi_noeppi.mods.bongo.easter.world;

import io.github.noeppi_noeppi.libx.mod.registration.Registerable;
import io.github.noeppi_noeppi.mods.bongo.ModBlocks;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockEgg;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.material.Material;
import net.minecraft.fluid.Fluids;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ISeedReader;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.Biomes;
import net.minecraft.world.gen.ChunkGenerator;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.NoFeatureConfig;

import javax.annotation.Nonnull;
import java.util.Random;

public class CommonEggFeature extends Feature<NoFeatureConfig> implements Registerable {

    private final ConfiguredFeature<?, ?> feature;
    
    public CommonEggFeature() {
        super(NoFeatureConfig.field_236558_a_);
        this.feature = this.withConfiguration(new NoFeatureConfig());
    }

    public ConfiguredFeature<?, ?> getFeature() {
        return feature;
    }

    @Override
    public boolean generate(@Nonnull ISeedReader world, @Nonnull ChunkGenerator generator, @Nonnull Random random, @Nonnull BlockPos pos, @Nonnull NoFeatureConfig config) {
        boolean success = false;
        for (int i = 0; i < 2; i++) {
            if (tryGenerate(world, generator, random, new BlockPos(pos.getX() + random.nextInt(16), 0, pos.getZ() + random.nextInt(16)))) {
                success = true;
            }
        }
        return success;
    }
    
    private boolean tryGenerate(@Nonnull ISeedReader world, @Nonnull ChunkGenerator generator, @Nonnull Random random, @Nonnull BlockPos hor) {
        Biome biome = world.getBiome(hor);
        if (biome.getCategory() == Biome.Category.THEEND) {
            if (random.nextInt(8) == 0) {
                BlockPos pos = highestFreeBlock(world, hor);
                return place(world, pos, ModBlocks.enderEgg, random);
            }
        } else if (biome.getCategory() == Biome.Category.NETHER && !Biomes.BASALT_DELTAS.getLocation().equals(biome.getRegistryName())) {
            if (random.nextInt(5) < 3) {
                BlockPos pos = lowerHalfAnyFreeBlockGround(world, hor, random);
                return place(world, pos, ModBlocks.netherEgg, random);
            }
        } else if (biome.getCategory() == Biome.Category.FOREST
                || biome.getCategory() == Biome.Category.JUNGLE
                || biome.getCategory() == Biome.Category.PLAINS
                || biome.getCategory() == Biome.Category.TAIGA
                || biome.getCategory() == Biome.Category.SAVANNA
                || biome.getCategory() == Biome.Category.SWAMP) {
            if (random.nextInt(8) == 0) {
                BlockPos pos = highestFreeBlock(world, hor);
                return place(world, pos, ModBlocks.woodEgg, random);
            }
        } else if (biome.getCategory() == Biome.Category.OCEAN) {
            if (random.nextInt(8) == 0) {
                BlockPos pos = highestFreeBlock(world, hor);
                return place(world, pos, ModBlocks.oceanEgg, random);
            }
        }
        if (biome.getCategory() != Biome.Category.OCEAN
                && biome.getCategory() != Biome.Category.NETHER
                && biome.getCategory() != Biome.Category.THEEND) {
            if (random.nextInt(4) == 0) {
                BlockPos pos = lowestFreeBlock(world, hor);
                if (pos.getY() < 50 && pos.getY() > 4) {
                    return place(world, pos, ModBlocks.rockEgg, random);
                }
            }
        }
        return false;
    }

    private BlockPos highestFreeBlock(ISeedReader world, BlockPos pos) {
        BlockPos.Mutable mpos = new BlockPos.Mutable(pos.getX(), world.getHeight() - 1, pos.getZ());
        while (mpos.getY() > 0 && isPassThroughBlock(world, mpos))
            mpos.move(0, -1, 0);
        return mpos.toImmutable().up();
    }
    
    private BlockPos lowestFreeBlock(ISeedReader world, BlockPos pos) {
        BlockPos.Mutable mpos = new BlockPos.Mutable(pos.getX(), 0, pos.getZ());
        while (mpos.getY() < world.getHeight() - 1 && !isPassThroughBlock(world, mpos))
            mpos.move(0, 1, 0);
        return mpos.toImmutable();
    }
    
    private BlockPos lowerHalfAnyFreeBlockGround(ISeedReader world, BlockPos pos, Random random) {
        BlockPos.Mutable mpos = new BlockPos.Mutable(pos.getX(), random.nextInt((world.getHeight() / 2) - 1), pos.getZ());
        while (mpos.getY() > 0 && isPassThroughBlock(world, mpos))
            mpos.move(0, -1, 0);
        return mpos.toImmutable().up();
    }
    
    private boolean place(ISeedReader world, BlockPos pos, BlockEgg<?> egg, Random random) {
        BlockState down = world.getBlockState(pos.down());
        if (down.getBlock() instanceof BlockEgg || !down.isSolid() || !canReplace(world, pos)) {
            return false;
        }
        if (world.getBlockState(pos.down()).getMaterial() == Material.LAVA) {
            world.setBlockState(pos.down(), Blocks.NETHERRACK.getDefaultState(), 2);
        }
        boolean water = world.getBlockState(pos).getFluidState().getFluid().isEquivalentTo(Fluids.WATER);
        world.setBlockState(pos, egg.getDefaultState()
                .with(BlockStateProperties.HORIZONTAL_FACING, Direction.byHorizontalIndex(random.nextInt(4)))
                .with(BlockStateProperties.WATERLOGGED, water), 2);
        return true;
    }

    private boolean isPassThroughBlock(ISeedReader world, BlockPos pos) {
        if (world.isAirBlock(pos)) {
            return true;
        } else {
            BlockState state = world.getBlockState(pos);
            return (state.getMaterial().isReplaceable() && state.getMaterial() != Material.LAVA) || state.getMaterial() == Material.LEAVES;
        }
    }

    private boolean canReplace(ISeedReader world, BlockPos pos) {
        return isPassThroughBlock(world, pos) && world.getBlockState(pos).getMaterial() != Material.LEAVES;
    }
}
