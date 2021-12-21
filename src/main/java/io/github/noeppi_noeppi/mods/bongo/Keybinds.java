package io.github.noeppi_noeppi.mods.bongo;

import net.minecraft.client.KeyMapping;
import net.minecraftforge.client.ClientRegistry;

public class Keybinds {

    public static final KeyMapping BIG_OVERLAY = new KeyMapping("bongo.big_overlay", 'Y', "key.categories.ui");

    public static void init() {
        ClientRegistry.registerKeyBinding(BIG_OVERLAY);
    }
}
