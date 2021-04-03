package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEnergizedEgg extends TileEntityBase implements ITickableTileEntity {

    public TileEnergizedEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public void tick() {
        if (world != null && world.isRemote && (world.getGameTime() + this.getPos().getX() - (2l * this.getPos().getZ())) % 60 == 0) {
            world.addParticle(ParticleTypes.FLASH, getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, 0, 0, 0);
        }
    }
}
