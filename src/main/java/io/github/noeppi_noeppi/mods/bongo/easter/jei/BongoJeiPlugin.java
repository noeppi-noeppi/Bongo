package io.github.noeppi_noeppi.mods.bongo.easter.jei;

import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.easter.EggHandler;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.block.Block;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nonnull;
import java.util.stream.Collectors;

@JeiPlugin
public class BongoJeiPlugin implements IModPlugin {

    public static final ResourceLocation UID = new ResourceLocation(BongoMod.getInstance().modid, "jeiplugin");
    
    @Nonnull
    @Override
    public ResourceLocation getPluginUid() {
        return UID;
    }

    @Override
    public void registerCategories(@Nonnull IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(
                new BunnyCategory(registration.getJeiHelpers().getGuiHelper())
        );
    }

    @Override
    public void registerRecipes(@Nonnull IRecipeRegistration registration) {
        registration.addRecipes(EggHandler.BUNNY.entrySet().stream()
                .map(e -> Pair.of(ForgeRegistries.ITEMS.getValue(e.getKey()), e.getValue().asItem()))
                .filter(e -> e.getKey() != null)
                .map(e -> new BunnyRecipe(new ItemStack(e.getKey()), new ItemStack(e.getValue())))
                .collect(Collectors.toList()), BunnyCategory.UID);
        for (Block natural : EggHandler.NATURAL) {
            registration.addIngredientInfo(new ItemStack(natural), VanillaTypes.ITEM, new TranslationTextComponent("bongo.easter.generate_in_world"));
        }
    }
}
