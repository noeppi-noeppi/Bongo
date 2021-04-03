package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockEgg;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class BlockEnergizedEgg extends BlockEgg<TileEnergizedEgg> {

    public BlockEnergizedEgg(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockEnergizedEgg(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileEnergizedEgg.class, properties, itemProperties.setISTER(() -> ItemStackRenderer::get));
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
        ClientRegistry.bindTileEntityRenderer(getTileType(), RenderEnergizedEgg::new);
    }
}
