package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileMysteriousEgg extends TileEntityBase implements ITickableTileEntity {

    public TileMysteriousEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public void tick() {
        if (world != null && world.isRemote) {
            for (int i = 0; i < 20; i++) {
                world.addParticle(ParticleTypes.REVERSE_PORTAL, getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5,
                        (world.rand.nextDouble() - 0.5) * 0.04, (world.rand.nextDouble() - 0.5) * 0.04, (world.rand.nextDouble() - 0.5) * 0.04);
            }
        }
    }
}
