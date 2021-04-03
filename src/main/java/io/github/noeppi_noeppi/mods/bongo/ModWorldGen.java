package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.libx.annotation.RegisterClass;
import io.github.noeppi_noeppi.mods.bongo.easter.world.CommonEggFeature;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraft.world.biome.MobSpawnInfo;
import net.minecraft.world.gen.GenerationStage;
import net.minecraftforge.event.world.BiomeLoadingEvent;

@RegisterClass
public class ModWorldGen {
    
    public static final CommonEggFeature commonEggs = new CommonEggFeature();
    
    public static void loadBiome(BiomeLoadingEvent event) {
        event.getGeneration().withFeature(GenerationStage.Decoration.TOP_LAYER_MODIFICATION, commonEggs.getFeature());
        event.getSpawns().withSpawner(EntityClassification.CREATURE, new MobSpawnInfo.Spawners(EntityType.RABBIT, 10, 1, 3));
    }
}
