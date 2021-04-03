package io.github.noeppi_noeppi.mods.bongo.datagen;

import io.github.noeppi_noeppi.libx.data.provider.BlockTagProviderBase;
import io.github.noeppi_noeppi.libx.data.provider.ItemTagProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ItemTagProvider extends ItemTagProviderBase {

	public ItemTagProvider(ModX mod, DataGenerator generatorIn, ExistingFileHelper fileHelper, BlockTagProviderBase blockTags) {
		super(mod, generatorIn, fileHelper, blockTags);
	}

	@Override
	protected void registerTags() {

	}
}
