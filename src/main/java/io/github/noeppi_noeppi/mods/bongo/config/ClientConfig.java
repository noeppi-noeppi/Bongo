package io.github.noeppi_noeppi.mods.bongo.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfig {

    public static final ForgeConfigSpec CLIENT_CONFIG;
    private static final ForgeConfigSpec.Builder CLIENT_BUILDER = new ForgeConfigSpec.Builder();

    static {
        init(CLIENT_BUILDER);
        CLIENT_CONFIG = CLIENT_BUILDER.build();
    }

    public static ForgeConfigSpec.ConfigValue<Double> bongoMapScaleFactor;

    public static void init(ForgeConfigSpec.Builder builder) {
        bongoMapScaleFactor = builder.comment("Controls the size of the bongo map.").define("bongo_map_scale_factor", 1d);
    }
}
