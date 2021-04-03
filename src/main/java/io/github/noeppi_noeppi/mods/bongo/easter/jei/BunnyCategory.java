package io.github.noeppi_noeppi.mods.bongo.easter.jei;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.noeppi_noeppi.mods.bongo.BongoMod;
import io.github.noeppi_noeppi.mods.bongo.ModBlocks;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

public class BunnyCategory implements IRecipeCategory<BunnyRecipe> {

    public static final ResourceLocation UID = new ResourceLocation(BongoMod.getInstance().modid, "bunny");

    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable bunny;
    private final IDrawable slot;
    private final String localizedName;

    public BunnyCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(90, 36);
        this.icon = guiHelper.createDrawableIngredient(new ItemStack(ModBlocks.basket));
        this.bunny = guiHelper.drawableBuilder(new ResourceLocation(BongoMod.getInstance().modid, "textures/icon/stat/eggs_collected.png"), 0, 0, 32, 32).setTextureSize(32, 32).build();
        this.slot = guiHelper.getSlotDrawable();
        this.localizedName = I18n.format("bongo.bunny.jei");
    }

    @Nonnull
    @Override
    public ResourceLocation getUid() {
        return UID;
    }

    @Nonnull
    @Override
    public Class<? extends BunnyRecipe> getRecipeClass() {
        return BunnyRecipe.class;
    }

    @Nonnull
    @Override
    public String getTitle() {
        return localizedName;
    }

    @Nonnull
    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Nonnull
    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setIngredients(@Nonnull BunnyRecipe recipe, @Nonnull IIngredients ii) {
        ii.setInputLists(VanillaTypes.ITEM, ImmutableList.of(
                ImmutableList.of(recipe.getInput())
        ));
        ii.setOutputLists(VanillaTypes.ITEM, ImmutableList.of(
                ImmutableList.of(recipe.getOutput())
        ));
    }

    @Override
    public void setRecipe(@Nonnull IRecipeLayout layout, @Nonnull BunnyRecipe recipe, @Nonnull IIngredients ii) {
        layout.getItemStacks().init(0, true, 9, 9);
        layout.getItemStacks().init(1, false, 63, 9);
        layout.getItemStacks().set(ii);
    }

    @Override
    public void draw(@Nonnull BunnyRecipe recipe, @Nonnull MatrixStack matrixStack, double mouseX, double mouseY) {
        this.slot.draw(matrixStack, 9, 9);
        this.slot.draw(matrixStack, 63, 9);
        this.bunny.draw(matrixStack, 29, 2);
    }
}
