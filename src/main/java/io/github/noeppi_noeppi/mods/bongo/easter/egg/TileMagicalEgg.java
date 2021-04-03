package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import com.ma.api.particles.ParticleInit;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileMagicalEgg extends TileEntityBase implements ITickableTileEntity {

    public TileMagicalEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public void tick() {
        if (world != null && world.isRemote) {
            for (int i = 0; i < 10; i++) {
                world.addParticle(ParticleInit.ARCANE_RANDOM.get(), getPos().getX() + 0.5, getPos().getY() + 0.5, getPos().getZ() + 0.5, 0.025, 0.03, 0.025);
            }
        }
    }
}
