package io.github.noeppi_noeppi.mods.bongo.datagen;

import io.github.noeppi_noeppi.libx.data.provider.BlockLootProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.mods.bongo.ModBlocks;
import net.minecraft.data.DataGenerator;

public class BlockLootProvider extends BlockLootProviderBase {

	public BlockLootProvider(ModX mod, DataGenerator generator) {
		super(mod, generator);
	}

	@Override
	protected void setup() {
		copyNBT(ModBlocks.basket, "items");
	}
}
