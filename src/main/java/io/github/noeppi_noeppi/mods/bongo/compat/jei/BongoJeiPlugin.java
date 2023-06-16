package io.github.noeppi_noeppi.mods.bongo.compat.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Required to get the JEI runtime
@JeiPlugin
public class BongoJeiPlugin implements IModPlugin {
    
    // Hardcoded mod id as JEI might load this before our own mod.
    public static ResourceLocation ID = new ResourceLocation("bongo", "jeiplugin");
    
    @Nullable
    public static IJeiRuntime runtime = null;

    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }

    @Override
    public void onRuntimeAvailable(@Nonnull IJeiRuntime runtime) {
        BongoJeiPlugin.runtime = runtime;
    }
}
