package io.github.noeppi_noeppi.mods.bongo.datagen;

import io.github.noeppi_noeppi.libx.data.provider.BlockTagProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;

public class BlockTagProvider extends BlockTagProviderBase {

	public BlockTagProvider(ModX mod, DataGenerator generatorIn, ExistingFileHelper fileHelper) {
		super(mod, generatorIn, fileHelper);
	}

	@Override
	protected void setup() {
		
	}
}
