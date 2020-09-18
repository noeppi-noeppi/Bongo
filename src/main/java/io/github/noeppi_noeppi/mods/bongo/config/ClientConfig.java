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
    public static ForgeConfigSpec.ConfigValue<Boolean> addItemTooltips;

    public static void init(ForgeConfigSpec.Builder builder) {
        bongoMapScaleFactor = builder.comment("Controls the size of the bongo map.").define("bongo_map_scale_factor", 1d);
        addItemTooltips = builder.comment("Whether bongo should add tooltips to items in the bingo card. This may cause a short spike of lagg when a game is created.").define("add_item_tooltips", true);
    }
}
