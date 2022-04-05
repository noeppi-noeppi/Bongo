package io.github.noeppi_noeppi.mods.bongo.datagen;

import io.github.noeppi_noeppi.libx.data.provider.BlockStateProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.ModBlocks;
import io.github.noeppi_noeppi.mods.bongo.easter.BlockEgg;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ModelFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BlockStateProvider extends BlockStateProviderBase {

    public static final ResourceLocation EGG_PARENT = new ResourceLocation(BongoMod.getInstance().modid, "block/egg");

    private final Map<Block, ResourceLocation> eggTextures = new HashMap<>();
    
    public BlockStateProvider(ModX mod, DataGenerator generator, ExistingFileHelper fileHelper) {
        super(mod, generator, new ExistingFileHelper(Collections.emptyList(), Collections.emptySet(), false));
    }

    @Override
    protected void setup() {
        manualModel(ModBlocks.basket);
        egg(ModBlocks.netherEgg, new ResourceLocation("minecraft", "block/ancient_debris_side"));
        egg(ModBlocks.rockEgg, new ResourceLocation("minecraft", "block/anvil"));
        egg(ModBlocks.woodEgg, new ResourceLocation("minecraft", "block/barrel_bottom"));
        egg(ModBlocks.enderEgg, new ResourceLocation("minecraft", "block/purpur_block"));
        egg(ModBlocks.oceanEgg, new ResourceLocation("minecraft", "block/prismarine"));
        egg(ModBlocks.mechanicalEgg, new ResourceLocation("create", "block/brass_block"));
        egg(ModBlocks.magicalEgg, new ResourceLocation("mana-and-artifice", "block/oreblockchimerite"));
        egg(ModBlocks.natureEgg, new ResourceLocation("botania", "block/livingwood"));
        egg(ModBlocks.technologyEgg, new ResourceLocation("mekanism", "block/block_osmium"));
        egg(ModBlocks.mysteriousEgg, new ResourceLocation("cyclic", "blocks/eye/teleport"));
        egg(ModBlocks.energizedEgg, new ResourceLocation("powah", "block/blazing_crystal_block"));
        egg(ModBlocks.dreamingEgg, new ResourceLocation("feywild", "block/spring_tree_log"));
        egg(ModBlocks.iconicEgg, new ResourceLocation("psi", "blocks/psigem_block"));
        egg(ModBlocks.agonyEgg, new ResourceLocation("bloodmagic", "block/largebloodstonebrick"));
        egg(ModBlocks.evilEgg, new ResourceLocation("eidolon", "block/arcane_gold_block"));
    }
    
    private void egg(Block block, ResourceLocation texture) {
        eggTextures.put(block, texture);
    }

    @Override
    protected void defaultState(ResourceLocation id, Block block, ModelFile model) {
        super.defaultState(id, block, model);
    }

    @Override
    protected ModelFile defaultModel(ResourceLocation id, Block block) {
        if (block instanceof BlockEgg && eggTextures.containsKey(block)) {
            return this.models().withExistingParent(id.getPath(), EGG_PARENT)
                    .texture("egg", eggTextures.get(block));
        } else {
            return super.defaultModel(id, block);
        }
    }
}