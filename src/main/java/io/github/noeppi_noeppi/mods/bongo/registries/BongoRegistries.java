package io.github.noeppi_noeppi.mods.bongo.registries;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryBuilder;

public class BongoRegistries {

    public static final IForgeRegistry<BongoPlayerTeleporter> TELEPORTER = new RegistryBuilder<BongoPlayerTeleporter>()
            .setName(new ResourceLocation(BongoMod.getInstance().modid, "teleporters"))
            .setType(BongoPlayerTeleporter.class)
            .disableOverrides()
            .create();

    public static void initRegistries(RegistryEvent.NewRegistry event) {
        //
    }
}
