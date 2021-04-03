package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockEgg;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class BlockMechanicalEgg extends BlockEgg<TileEntityBase> {

    public BlockMechanicalEgg(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockMechanicalEgg(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileEntityBase.class, properties, itemProperties.setISTER(() -> ItemStackRenderer::get));
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void registerClient(ResourceLocation id) {
        super.registerClient(id);
        ItemStackRenderer.addRenderTile(getTileType(), false);
    }
    
    @OnlyIn(Dist.CLIENT)
    @Override
    public void registerTESR() {
        ClientRegistry.bindTileEntityRenderer(getTileType(), RenderMechanicalEgg::new);
    }
}
