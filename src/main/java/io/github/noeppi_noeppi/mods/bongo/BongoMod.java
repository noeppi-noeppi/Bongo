package io.github.noeppi_noeppi.mods.bongo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
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
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimplePreparableReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import org.moddingx.libx.mod.ModX;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Nonnull;

@Mod("bongo")
public final class BongoMod extends ModX {
    
    public static final Logger logger = LoggerFactory.getLogger("bongo");
    
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

        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerStuff);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::reloadClientResources);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
            FMLJavaModLoadingContext.get().getModEventBus().addListener(this::registerGUIs);
            FMLJavaModLoadingContext.get().getModEventBus().addListener(Keybinds::registerKeyBinds);
        });
        
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
        TaskTypes.registerType(TaskTypeEffect.INSTANCE);
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

        ArgumentTypeInfos.registerByClass(GameTasksArgument.class, GameTasksArgument.Info.INSTANCE);
        ArgumentTypeInfos.registerByClass(GameSettingsArgument.class, GameSettingsArgument.Info.INSTANCE);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    protected void clientSetup(FMLClientSetupEvent event) {
        MinecraftForge.EVENT_BUS.addListener(RenderOverlay::renderGuiPart);
    }

    @OnlyIn(Dist.CLIENT)
    private void registerGUIs(RegisterGuiOverlaysEvent event) {
        event.registerAbove(VanillaGuiOverlay.BOSS_EVENT_PROGRESS.id(), "bongo", RenderOverlay.INSTANCE);
    }
    
    private void registerStuff(RegisterEvent event) {
        event.register(Registries.COMMAND_ARGUMENT_TYPE, this.resource("tasks"), () -> GameTasksArgument.Info.INSTANCE);
        event.register(Registries.COMMAND_ARGUMENT_TYPE, this.resource("settings"), () -> GameSettingsArgument.Info.INSTANCE);
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
