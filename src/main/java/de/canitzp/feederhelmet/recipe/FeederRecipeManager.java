package de.canitzp.feederhelmet.recipe;

import de.canitzp.feederhelmet.FeederHelmet;
import de.canitzp.feederhelmet.module.IHelmetModule;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.ArrayList;
import java.util.List;

public class FeederRecipeManager {

    public static void injectRecipes(Level level){
        if(level.dimension() != Level.OVERWORLD){
            return;
        }
        FeederHelmet.LOGGER.info("Feeder Helmet recipe injecting...");
        RecipeManager recipeManager = level.getRecipeManager();

        // list which the old recipes are replaced with. This should include all existing recipes and the new ones, before recipeManager#replaceRecipes is called!
        List<RecipeHolder<?>> allNewRecipes = new ArrayList<>();
        for(IHelmetModule module : FeederHelmet.MODULES){
            for(Item helmet : BuiltInRegistries.ITEM){
                if(module.isModuleApplicableTo(helmet.getDefaultInstance())){
                    ResourceLocation helmetKey = BuiltInRegistries.ITEM.getKey(helmet);
                    // create recipe id for creation recipe
                    ResourceLocation creationCraftingId = new ResourceLocation(FeederHelmet.MODID, module.getTagName() + "_creation_" + helmetKey.getNamespace() + "_" + helmetKey.getPath());
                    // create recipe id for removal recipe
                    ResourceLocation removalCraftingId = new ResourceLocation(FeederHelmet.MODID, module.getTagName() + "_removal_" + helmetKey.getNamespace() + "_" + helmetKey.getPath());
                    // create recipe for creation
                    Recipe<?> creationRecipe = FeederRecipeManager.creationRecipe(module, helmet);
                    // create recipe for removal
                    Recipe<?> removalRecipe = FeederRecipeManager.removalRecipe(module, helmet);

                    // add creation recipe to recipes list
                    if(recipeManager.getRecipeIds().noneMatch(resourceLocation -> resourceLocation.equals(creationCraftingId))){
                        allNewRecipes.add(new RecipeHolder<>(creationCraftingId, creationRecipe));
                        FeederHelmet.LOGGER.info(String.format("Feeder Helmet created %s recipe for %s with id '%s'", module.getTagName(), helmetKey, creationCraftingId));
                    }
                    // add removal recipe to recipes list
                    if(recipeManager.getRecipeIds().noneMatch(resourceLocation -> resourceLocation.equals(removalCraftingId))){
                        allNewRecipes.add(new RecipeHolder<>(removalCraftingId, removalRecipe));
                        FeederHelmet.LOGGER.info(String.format("Feeder Helmet created %s recipe for %s with id '%s'", module.getTagName(), helmetKey, removalCraftingId));
                    }
                }
            }
        }

        try{
            // add all existing recipes, since we're gonna replace them
            allNewRecipes.addAll(recipeManager.getRecipes());
            recipeManager.replaceRecipes(allNewRecipes);
        } catch(IllegalStateException e){
            FeederHelmet.LOGGER.error("Feeder Helmet: Illegal recipe replacement caught! Report this to author immediately!", e);
        }
    }

    public static Recipe<?> creationRecipe(final IHelmetModule module, final Item helmet){
        ItemStack outputStack = helmet.getDefaultInstance();

        FeederHelmet.addModule(outputStack, module.getTagName());

        return new RecipeModuleAddition(helmet, module.getTagName(), outputStack);
    }

    public static Recipe<?> removalRecipe(final IHelmetModule module, final Item helmet){
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(helmet));
        ItemStack outputStack = helmet.getDefaultInstance();
        return new ShapelessRecipe("", CraftingBookCategory.EQUIPMENT, outputStack, ingredients){
            // copy nbt tag from helmet to new helmet, also delete SolarHelmet tag
            @Override
            public ItemStack assemble(CraftingContainer container, HolderLookup.Provider access) {
                ItemStack assembled = super.assemble(container, access);
                ItemStack inputStack = ItemStack.EMPTY;
                for (int slotId = 0; slotId < container.getContainerSize(); slotId++) {
                    if(!container.getItem(slotId).isEmpty()){
                        inputStack = container.getItem(slotId).copy();
                        break;
                    }
                }
                if(!inputStack.isEmpty()){
                    if(inputStack.has(FeederHelmet.DC_MODULES)){
                        FeederHelmet.removeModule(inputStack, module.getTagName());
                    }
                    // Copy all components to assembled stack
                    assembled.applyComponents(inputStack.getComponents());
                }
                return assembled;
            }

            // only match if the input helmet has an enabled SolarHelmet module
            @Override
            public boolean matches(CraftingContainer container, Level level) {
                boolean matches = super.matches(container, level);
                if(!matches){
                    return false;
                }
                ItemStack inputStack = ItemStack.EMPTY;
                for (int slotId = 0; slotId < container.getContainerSize(); slotId++) {
                    if(!container.getItem(slotId).isEmpty()){
                        inputStack = container.getItem(slotId);
                        break;
                    }
                }
                if (inputStack.isEmpty()) {
                    return false; // this "should" never happen
                }
                return FeederHelmet.hasModule(inputStack, module.getTagName());
            }

            @Override
            public NonNullList<ItemStack> getRemainingItems(CraftingContainer container) {
                NonNullList<ItemStack> remainingItems = super.getRemainingItems(container);
                remainingItems.set(0, FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get().getDefaultInstance());
                return remainingItems;
            }
        };
    }
}
