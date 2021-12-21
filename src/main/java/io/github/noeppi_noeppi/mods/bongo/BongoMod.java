package io.github.noeppi_noeppi.mods.bongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.mods.bongo.command.BongoCommands;
import io.github.noeppi_noeppi.mods.bongo.command.arg.GameSettingsArgument;
import io.github.noeppi_noeppi.mods.bongo.command.arg.GameTasksArgument;
import io.github.noeppi_noeppi.mods.bongo.compat.CuriosIntegration;
import io.github.noeppi_noeppi.mods.bongo.compat.MineMentionIntegration;
import io.github.noeppi_noeppi.mods.bongo.compat.SkyblockIntegration;
import io.github.noeppi_noeppi.mods.bongo.config.ClientConfig;
import io.github.noeppi_noeppi.mods.bongo.effect.DefaultEffects;
import io.github.noeppi_noeppi.mods.bongo.network.BongoNetwork;
import io.github.noeppi_noeppi.mods.bongo.render.CrownRenderer;
import io.github.noeppi_noeppi.mods.bongo.render.RenderOverlay;
import io.github.noeppi_noeppi.mods.bongo.task.*;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporterDefault;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporterNothing;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporterStandard;
import io.github.noeppi_noeppi.mods.bongo.teleporters.PlayerTeleporters;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import javax.annotation.Nonnull;

@Mod("bongo")
public final class BongoMod extends ModX {

    private static BongoMod instance;
    private static BongoNetwork network;

    public static final Gson GSON = net.minecraft.Util.make(() -> {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.disableHtmlEscaping();
        gsonbuilder.setLenient();
        return gsonbuilder.create();
    });
    public static final Gson PRETTY_GSON = net.minecraft.Util.make(() -> {
        GsonBuilder gsonbuilder = new GsonBuilder();
        gsonbuilder.disableHtmlEscaping();
        gsonbuilder.setLenient();
        gsonbuilder.setPrettyPrinting();
        return gsonbuilder.create();
    });

    public BongoMod() {
        instance = this;
        network = new BongoNetwork(this);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfig.CLIENT_CONFIG);
        
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::reloadClientResources);
        
        MinecraftForge.EVENT_BUS.register(new EventListener());
        MinecraftForge.EVENT_BUS.register(new DefaultEffects());
        MinecraftForge.EVENT_BUS.addListener(BongoCommands::register);

        if (ModList.get().isLoaded("curios")) {
            MinecraftForge.EVENT_BUS.register(new CuriosIntegration());
        }

        if (ModList.get().isLoaded("skyblockbuilder")) {
            SkyblockIntegration.init();
        }
    }

    @Nonnull
    public static BongoMod getInstance() {
        return instance;
    }

    @Nonnull
    public static BongoNetwork getNetwork() {
        return network;
    }

    @Override
    protected void setup(FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded("skyblockbuilder")) {
            SkyblockIntegration.setup();
        }
        
        TaskTypes.registerType(TaskTypeEmpty.INSTANCE);
        TaskTypes.registerType(TaskTypeItem.INSTANCE);
        TaskTypes.registerType(TaskTypeAdvancement.INSTANCE);
        TaskTypes.registerType(TaskTypeEntity.INSTANCE);
        TaskTypes.registerType(TaskTypeBiome.INSTANCE);
        TaskTypes.registerType(TaskTypePotion.INSTANCE);
        TaskTypes.registerType(TaskTypeStat.INSTANCE);
        TaskTypes.registerType(TaskTypeTag.INSTANCE);

        PlayerTeleporters.registerTeleporter(PlayerTeleporterDefault.INSTANCE);
        PlayerTeleporters.registerTeleporter(PlayerTeleporterStandard.INSTANCE);
        PlayerTeleporters.registerTeleporter(PlayerTeleporterNothing.INSTANCE);

        if (ModList.get().isLoaded("skyblockbuilder")) {
            PlayerTeleporters.registerTeleporter(SkyblockIntegration.Teleporter.INSTANCE);
        }
        
        if (ModList.get().isLoaded("minemention")) {
            MineMentionIntegration.setup();
        }
        
        ArgumentTypes.register(modid + "_bongotasks", GameTasksArgument.class, new GameTasksArgument.Serializer());
        ArgumentTypes.register(modid + "_bongosettings", GameSettingsArgument.class, new GameSettingsArgument.Serializer());
    }

    @Override
    protected void clientSetup(FMLClientSetupEvent event) {
        Keybinds.init();
        MinecraftForge.EVENT_BUS.register(new RenderOverlay());
    }
    
    private void reloadClientResources(RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new SimplePreparableReloadListener<Void>() {

            @Nonnull
            @Override
            protected Void prepare(@Nonnull ResourceManager rm, @Nonnull ProfilerFiller filler) {
                return null;
            }

            @Override
            protected void apply(@Nonnull Void value, @Nonnull ResourceManager rm, @Nonnull ProfilerFiller filler) {
                CrownRenderer.register();
            }
        });
    }
}
