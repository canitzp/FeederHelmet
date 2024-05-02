package de.canitzp.feederhelmet;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;

public class ItemStackUtil {

    public static String getItemStackResourceLocationString(ItemStack stack){
        return BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
    }
    
    public static boolean isFoodBlacklisted(ItemStack stack){
        return FeederConfig.GENERAL.FOOD_BLACKLIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }
    
    public static boolean isFoodWhitelisted(ItemStack stack){
        return FeederConfig.GENERAL.FOOD_WHITELIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }
    
    public static boolean isSmeltable(Level level, ItemStack stack){
        return level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING).stream().anyMatch(furnaceRecipe -> furnaceRecipe.value().getIngredients().stream().anyMatch(ingredient -> ingredient.test(stack)));
    }
    
    public static boolean isEatable(ItemStack stack){
        return stack.has(DataComponents.FOOD);
    }
    
    public static boolean isHelmetBlacklisted(ItemStack stack){
        return FeederConfig.GENERAL.HELMET_BLACKLIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }
    
    public static boolean isHelmetWhitelisted(ItemStack stack){
        return FeederConfig.GENERAL.HELMET_WHITELIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }

}
