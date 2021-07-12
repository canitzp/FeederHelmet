package de.canitzp.feederhelmet;

import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.world.World;

public class ItemStackUtil {

    public static String getItemStackResourceLocationString(ItemStack stack){
        return stack.getItem().getRegistryName().toString();
    }
    
    public static boolean isFoodBlacklisted(ItemStack stack){
        return FeederConfig.GENERAL.FOOD_BLACKLIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }
    
    public static boolean isFoodWhitelisted(ItemStack stack){
        return FeederConfig.GENERAL.FOOD_WHITELIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }
    
    public static boolean isSmeltable(World level, ItemStack stack){
        return level.getRecipeManager().getAllRecipesFor(IRecipeType.SMELTING).stream().anyMatch(furnaceRecipe -> furnaceRecipe.getIngredients().stream().anyMatch(ingredient -> ingredient.test(stack)));
    }
    
    public static boolean isEatable(ItemStack stack){
        return stack.getItem().isEdible();
    }
    
    public static boolean isHelmetBlacklisted(ItemStack stack){
        return FeederConfig.GENERAL.HELMET_BLACKLIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }
    
    public static boolean isHelmetWhitelisted(ItemStack stack){
        return FeederConfig.GENERAL.HELMET_WHITELIST.get().contains(ItemStackUtil.getItemStackResourceLocationString(stack));
    }

}
