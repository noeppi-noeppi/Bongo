package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.libx.render.ClientTickHandler;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vazkii.psi.client.fx.SparkleParticleData;

import java.awt.*;

public class TileIconicEgg extends TileEntityBase implements ITickableTileEntity {

    public TileIconicEgg(TileEntityType<?> tileEntityTypeIn) {
        super(tileEntityTypeIn);
    }
    
    @Override
    @OnlyIn(Dist.CLIENT)
    public void tick() {
        if (world != null && world.isRemote) {
            int hue = ((ClientTickHandler.ticksInGame % 360) + 360) % 360;
            int rgb = Color.HSBtoRGB(hue / 360f, 1, 1);
            SparkleParticleData data = SparkleParticleData.sparkle(1.2f, (rgb >>> 16) & 0xFF, (rgb >>> 8) & 0xFF, rgb & 0xFF, 3, 1, 1, 1);
            double angle = world.getGameTime() / 5f;
            world.addParticle(data, getPos().getX() + 0.5 + (float) Math.sin(angle) * 0.2, getPos().getY() + 0.9, getPos().getZ() + 0.5 + (float) Math.cos(angle) * 0.2, (float) Math.sin(angle) * 0.05, 0.07f, (float) Math.cos(angle) * 0.05);
        }
    }
}
