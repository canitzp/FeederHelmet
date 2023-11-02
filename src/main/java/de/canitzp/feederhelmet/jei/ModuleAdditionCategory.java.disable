package de.canitzp.feederhelmet.jei;

import de.canitzp.feederhelmet.FeederHelmet;
import de.canitzp.feederhelmet.recipe.RecipeModuleAddition;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import mezz.jei.common.Constants;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

public class ModuleAdditionCategory implements IRecipeCategory<RecipeModuleAddition> {

    private final IDrawable background;
    private final IDrawable icon;

    public ModuleAdditionCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createDrawable(Constants.RECIPE_GUI_VANILLA, 0, 168, 108, 18);
        this.icon = guiHelper.createDrawableItemStack(new ItemStack(Blocks.SMITHING_TABLE));
    }

    @Override
    public @NotNull RecipeType<RecipeModuleAddition> getRecipeType() {
        return JEIFeederHelmet.RECIPE_MODULE_ADDITION_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() {
        return Blocks.SMITHING_TABLE.getName();
    }

    @Override
    public @NotNull IDrawable getBackground() {
        return this.background;
    }

    @Override
    public @NotNull IDrawable getIcon() {
        return this.icon;
    }

    @Override
    public void setRecipe(@NotNull IRecipeLayoutBuilder builder, @NotNull RecipeModuleAddition recipe, @NotNull IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 19, 1).addIngredients(Ingredient.of(recipe.getBase()));

        builder.addSlot(RecipeIngredientRole.INPUT, 37, 1).addItemStack(FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get().getDefaultInstance());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 1)
                .addItemStack(recipe.getOutputStack());
    }

}
