package io.github.noeppi_noeppi.mods.bongo.easter;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import io.github.noeppi_noeppi.mods.bongo.ModBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.passive.RabbitEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Map;
import java.util.Set;

public class EggHandler {
    
    public static final Set<Block> NATURAL = ImmutableSet.of(
            ModBlocks.netherEgg,
            ModBlocks.rockEgg,
            ModBlocks.woodEgg,
            ModBlocks.enderEgg,
            ModBlocks.oceanEgg
    );

    public static final Map<ResourceLocation, BlockEgg<?>> BUNNY = ImmutableMap.<ResourceLocation, BlockEgg<?>>builder()
            .put(new ResourceLocation("create", "refined_radiance"), ModBlocks.mechanicalEgg)
            .put(new ResourceLocation("mana-and-artifice", "infused_silk"), ModBlocks.magicalEgg)
            .put(new ResourceLocation("botania", "rune_water"), ModBlocks.natureEgg)
            .put(new ResourceLocation("mekanism", "elite_control_circuit"), ModBlocks.technologyEgg)
            .put(new ResourceLocation("cyclic", "gem_amber"), ModBlocks.mysteriousEgg)
            .put(new ResourceLocation("powah", "player_transmitter_basic"), ModBlocks.energizedEgg)
            .build();
    
    public static boolean handleBunnyFood(RabbitEntity bunny, ItemStack food) {
        World world = bunny.getEntityWorld();
        ResourceLocation rl = food.getItem().getRegistryName();
        if (BUNNY.containsKey(rl)) {
            BlockEgg<?> block = BUNNY.get(rl);
            if (BUNNY.containsKey(rl)) {
                BlockPos center = bunny.getPosition();
                BlockPos target = null;
                for (int i = 0; i < 20 && target == null; i++) {
                    BlockPos attempt = new BlockPos(
                            world.rand.nextInt(48) - 24 + center.getX(),
                            world.rand.nextInt(32) - 16 + center.getY(),
                            world.rand.nextInt(48) - 24 + center.getZ()
                    );
                    if (validPosition(world, attempt)) {
                        target = attempt;
                    }
                }
                if (target == null) {
                    target = center.down();
                }
                world.setBlockState(target, block.getDefaultState()
                        .with(BlockStateProperties.HORIZONTAL_FACING, Direction.byHorizontalIndex(world.rand.nextInt(4)))
                        .with(BlockStateProperties.LIT, true), 3);
                BlockPos.Mutable wanderPos = target.toMutable();
                //noinspection deprecation
                while (world.getBlockState(wanderPos).isAir() && wanderPos.getY() >= 5) {
                    wanderPos.move(0, -1, 0);
                }
                bunny.getNavigator().tryMoveToXYZ(wanderPos.getX() + 0.5, wanderPos.getY() + 0.5, wanderPos.getZ() + 0.5, 2);
                return true;
            }
        }
        return false;
    }
    
    private static boolean validPosition(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        if (state.getBlock() == Blocks.BEDROCK || state.getBlock() == Blocks.END_PORTAL) {
            return false;
        }
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    if (x != 0 || y != 0 || z != 0) {
                        BlockState other = world.getBlockState(pos.add(x, y, z));
                        //noinspection deprecation
                        if (other.isAir() || other.getBlock() == Blocks.BEDROCK
                                || other.getBlock() == Blocks.END_PORTAL) {
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }
}
