package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.libx.mod.registration.TileEntityBase;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockBasket;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockEgg;
import io.github.noeppi_noeppi.mods.bongo.easter.egg.*;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.material.Material;

@RegisterClass
public class ModBlocks {
    
    public static final BlockBasket basket = new BlockBasket(BongoMod.getInstance(), AbstractBlock.Properties.create(Material.MISCELLANEOUS));
    public static final BlockEgg<TileEntityBase> netherEgg = new BlockEgg<>(BongoMod.getInstance(), TileEntityBase.class, AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileEntityBase> rockEgg = new BlockEgg<>(BongoMod.getInstance(), TileEntityBase.class, AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileEntityBase> woodEgg = new BlockEgg<>(BongoMod.getInstance(), TileEntityBase.class, AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileEntityBase> enderEgg = new BlockEgg<>(BongoMod.getInstance(), TileEntityBase.class, AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileEntityBase> oceanEgg = new BlockEgg<>(BongoMod.getInstance(), TileEntityBase.class, AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileEntityBase> mechanicalEgg = new BlockMechanicalEgg(BongoMod.getInstance(), AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileMagicalEgg> magicalEgg = new BlockMagicalEgg(BongoMod.getInstance(), AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileNatureEgg> natureEgg = new BlockNatureEgg(BongoMod.getInstance(), AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileEntityBase> technologyEgg = new BlockEgg<>(BongoMod.getInstance(), TileEntityBase.class, AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileMysteriousEgg> mysteriousEgg = new BlockMysteriousEgg(BongoMod.getInstance(), AbstractBlock.Properties.create(Material.DRAGON_EGG));
    public static final BlockEgg<TileEnergizedEgg> energizedEgg = new BlockEnergizedEgg(BongoMod.getInstance(), AbstractBlock.Properties.create(Material.DRAGON_EGG));
}
