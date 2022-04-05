package io.github.noeppi_noeppi.mods.bongo.easter.egg;

import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.libx.render.ItemStackRenderer;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockEgg;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.ClientRegistry;

import javax.annotation.Nonnull;

public class BlockIconicEgg extends BlockEgg<TileIconicEgg> {

    public BlockIconicEgg(ModX mod, Properties properties) {
        this(mod, properties, new Item.Properties());
    }

    public BlockIconicEgg(ModX mod, Properties properties, Item.Properties itemProperties) {
        super(mod, TileIconicEgg.class, properties, itemProperties.setISTER(() -> ItemStackRenderer::get));
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
        ClientRegistry.bindTileEntityRenderer(getTileType(), RenderIconicEgg::new);
    }

    @Nonnull
    @Override
    @SuppressWarnings("deprecation")
    public BlockRenderType getRenderType(@Nonnull BlockState state) {
        return BlockRenderType.ENTITYBLOCK_ANIMATED;
    }
}
