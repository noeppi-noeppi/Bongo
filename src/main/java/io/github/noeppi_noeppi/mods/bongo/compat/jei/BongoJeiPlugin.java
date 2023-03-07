package io.github.noeppi_noeppi.mods.bongo.compat.jei;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.runtime.IJeiRuntime;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

// Required to get the JEI runtime
@JeiPlugin
public class BongoJeiPlugin implements IModPlugin {
    
    public static ResourceLocation ID = BongoMod.getInstance().resource("jeiplugin");
    
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
