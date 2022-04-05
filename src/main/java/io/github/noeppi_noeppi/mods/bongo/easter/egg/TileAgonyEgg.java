package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import wayoftime.bloodmagic.common.item.BloodMagicItems;

public class TileAgonyEgg extends TileEntityBase implements ITickableTileEntity {

    public TileAgonyEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public void tick() {
        if (world != null && world.isRemote && Math.random() < 0.2) {
            double xPos = 0.3;
            double zPos = 0.5;
            Direction dir = getBlockState().get(BlockStateProperties.HORIZONTAL_FACING);
            if (dir.getAxis() == Direction.Axis.Z) {
                double tmp = zPos;
                zPos = xPos;
                xPos = 1 - tmp;
            }
            if (dir.getAxisDirection() == Direction.AxisDirection.NEGATIVE) {
                xPos = 1 - xPos;
                zPos = 1 - zPos;
            }
            IParticleData particle = new ItemParticleData(ParticleTypes.ITEM, new ItemStack(BloodMagicItems.WEAK_BLOOD_SHARD.get()));
            world.addParticle(particle, this.getPos().getX() + xPos, this.getPos().getY() + 0.95, this.getPos().getZ() + zPos, 0.1 * (Math.random() - 0.5), 0.1, 0.1 * (Math.random() - 0.5));
        }
    }
}
