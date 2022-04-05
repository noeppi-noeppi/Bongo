package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import elucent.eidolon.Registry;
import elucent.eidolon.particle.Particles;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;

public class TileEvilEgg extends TileEntityBase implements ITickableTileEntity {

    public TileEvilEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    public void tick() {
        if (world != null && world.isRemote) {
            Particles.create(Registry.FLAME_PARTICLE)
                    .setColor(0.2f, 0.2f, 1f)
                    .spawn(this.world, this.pos.getX() + 0.5, this.pos.getY() + 0.8, this.pos.getZ() + 0.5);
        }
    }
}
