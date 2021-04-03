package io.github.noeppi_noeppi.mods.bongo.datagen;

import io.github.noeppi_noeppi.libx.data.provider.recipe.RecipeProviderBase;
import io.github.noeppi_noeppi.libx.mod.ModX;
import io.github.noeppi_noeppi.mods.bongo.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Items;
import net.minecraft.tags.ItemTags;

import javax.annotation.Nonnull;
import java.util.Objects;
import java.util.function.Consumer;

import static net.minecraft.data.ShapedRecipeBuilder.shapedRecipe;

public class RecipeProvider extends RecipeProviderBase {

    public RecipeProvider(ModX mod, DataGenerator generator) {
        super(mod, generator);
    }

    @Override
    protected void registerRecipes(@Nonnull Consumer<IFinishedRecipe> consumer) {
        shapedRecipe(ModBlocks.basket)
                .patternLine("ss")
                .patternLine("pp")
                .key('s', Items.STICK)
                .key('p', ItemTags.PLANKS)
                .setGroup(Objects.requireNonNull(ModBlocks.basket.getRegistryName()).getPath())
                .addCriterion("has_item", hasItem(Items.STICK))
                .build(consumer);
    }
}
