package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileDreamingEgg extends TileEntityBase implements ITickableTileEntity {

    public TileDreamingEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public void tick() {
        if (world != null && world.isRemote && Math.random() < 0.05) {
            world.addParticle(ParticleTypes.NOTE, getPos().getX() + Math.random(), getPos().getY() + 0.7, getPos().getZ() + Math.random(), 0, 0, 0);
        }
    }
}
