package io.github.noeppi_noeppi.mods.bongo;

import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.ClientRegistry;

public class Keybinds {

    public static final KeyBinding BIG_OVERLAY = new KeyBinding("bongo.big_overlay", 'Y', "key.categories.ui");

    public static void init() {
        ClientRegistry.registerKeyBinding(BIG_OVERLAY);
    }
}
