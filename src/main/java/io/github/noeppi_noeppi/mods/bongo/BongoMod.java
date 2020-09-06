package io.github.noeppi_noeppi.mods.bongo;

import io.github.noeppi_noeppi.mods.bongo.command.BongoCommands;
import io.github.noeppi_noeppi.mods.bongo.command.arg.GameDefArgument;
import io.github.noeppi_noeppi.mods.bongo.command.arg.UppercaseEnumArgument;
import io.github.noeppi_noeppi.mods.bongo.network.BongoNetwork;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.task.*;
import io.github.noeppi_noeppi.mods.bongo.util.Util;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(BongoMod.MODID)
public class BongoMod {

    public static final String MODID = "bongo";
    public static final Logger LOGGER = LogManager.getLogger();

    public BongoMod() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::clientSetup);

        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.addListener(BongoCommands::register);
    }

    private void setup(FMLCommonSetupEvent event) {
        BongoNetwork.registerPackets();

        TaskTypes.registerType(TaskTypeEmpty.INSTANCE);
        TaskTypes.registerType(TaskTypeItem.INSTANCE);
        TaskTypes.registerType(TaskTypeAdvancement.INSTANCE);

        Util.registerGenericCommandArgument(MODID + "_upperenum", UppercaseEnumArgument.class, new UppercaseEnumArgument.Serializer());
        ArgumentTypes.register(MODID + "_bongogame", GameDefArgument.class, new GameDefArgument.Serialzier());
    }

    private void clientSetup(FMLClientSetupEvent event) {
        Keybinds.init();
        MinecraftForge.EVENT_BUS.register(new RenderOverlay());
    }
}