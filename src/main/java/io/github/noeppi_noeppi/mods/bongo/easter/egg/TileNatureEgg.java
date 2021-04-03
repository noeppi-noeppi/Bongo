package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import vazkii.botania.client.fx.WispParticleData;

public class TileNatureEgg extends TileEntityBase implements ITickableTileEntity {

    public TileNatureEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }

    @Override
    public void tick() {
        if (world != null && world.isRemote) {
            if (world.rand.nextFloat() < 0.3) {
                WispParticleData data = WispParticleData.wisp(0.4f, 0, 1, 0, 1.2f);
                world.addParticle(data, getPos().getX() + 0.5, getPos().getY(), getPos().getZ() + 0.5, (world.rand.nextDouble() - 0.5) * 0.05, 0.05f, (world.rand.nextDouble() - 0.5) * 0.05);
            }
            if (world.rand.nextFloat() < 0.3) {
                WispParticleData data = WispParticleData.wisp(0.4f, 0.2f, 0.2f, 1, 0.9f);
                world.addParticle(data, getPos().getX() + 0.5, getPos().getY(), getPos().getZ() + 0.5, (world.rand.nextDouble() - 0.5) * 0.05, 0.05f, (world.rand.nextDouble() - 0.5) * 0.05);
            }
        }
    }
}
