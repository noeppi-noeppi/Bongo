package io.github.noeppi_noeppi.mods.bongo.datagen;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

public class DataGenerators {
    
    public static void gatherData(GatherDataEvent evt) {
		if (evt.includeServer()) {
			evt.getGenerator().addProvider(new BlockLootProvider(BongoMod.getInstance(), evt.getGenerator()));
			BlockTagProvider blockTagProvider = new BlockTagProvider(BongoMod.getInstance(), evt.getGenerator(), evt.getExistingFileHelper());
			evt.getGenerator().addProvider(blockTagProvider);
			evt.getGenerator().addProvider(new ItemTagProvider(BongoMod.getInstance(), evt.getGenerator(), evt.getExistingFileHelper(), blockTagProvider));
			evt.getGenerator().addProvider(new BlockStateProvider(BongoMod.getInstance(), evt.getGenerator(), evt.getExistingFileHelper()));
			evt.getGenerator().addProvider(new ItemModelProvider(BongoMod.getInstance(), evt.getGenerator(), evt.getExistingFileHelper()));
			evt.getGenerator().addProvider(new RecipeProvider(BongoMod.getInstance(), evt.getGenerator()));
		}
	}
}
