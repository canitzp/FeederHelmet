package de.canitzp.feederhelmet.jei;

import de.canitzp.feederhelmet.FeederHelmet;
import de.canitzp.feederhelmet.recipe.RecipeModuleAddition;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.inventory.SmithingMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.jetbrains.annotations.NotNull;

@JeiPlugin
public class JEIFeederHelmet implements IModPlugin {

    public static final RecipeType<RecipeModuleAddition> RECIPE_MODULE_ADDITION_RECIPE_TYPE = RecipeType.create(FeederHelmet.MODID, "module_addition", RecipeModuleAddition.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return new ResourceLocation(FeederHelmet.MODID, "jei");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new ModuleAdditionCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(RECIPE_MODULE_ADDITION_RECIPE_TYPE, Minecraft.getInstance().level.getRecipeManager().getRecipes().stream().filter(recipe -> recipe instanceof RecipeModuleAddition).map(recipe -> (RecipeModuleAddition) recipe).toList());
    }

    @Override
    public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
        registration.addRecipeTransferHandler(SmithingMenu.class, MenuType.SMITHING, RECIPE_MODULE_ADDITION_RECIPE_TYPE, 0, 3, 3, 36);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(Blocks.SMITHING_TABLE), RECIPE_MODULE_ADDITION_RECIPE_TYPE);
    }
}
