package de.canitzp.feederhelmet;

import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class FeederRecipeManager {

    @SuppressWarnings("removal")
    public static Recipe<?> creationRecipe(final IHelmetModule module, final Item helmet, final ResourceLocation craftingId){
        ItemStack outputStack = helmet.getDefaultInstance();
        NBTHelper.addModule(module.getTagName(), outputStack);
        return new LegacyUpgradeRecipe(craftingId, Ingredient.of(helmet), Ingredient.of(FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get()), outputStack){
            @Override
            public @NotNull ItemStack assemble(@NotNull Container container, RegistryAccess access) {
                ItemStack assembled = super.assemble(container, access);
                NBTHelper.addModule(module.getTagName(), assembled);
                return assembled;
            }

            // checks if the helmet doesn't already have the module
            @Override
            public boolean matches(Container container, Level level) {
                boolean matches = super.matches(container, level);
                if(!matches){
                    return false;
                }
                ItemStack helmetInputStack = container.getItem(0);
                return !NBTHelper.isModulePresent(module.getTagName(), helmetInputStack);
            }
        };
    }

    public static Recipe<?> removalRecipe(final IHelmetModule module, final Item helmet, final ResourceLocation craftingId){
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.of(helmet));
        ItemStack outputStack = helmet.getDefaultInstance();
        return new ShapelessRecipe(craftingId, "", CraftingBookCategory.EQUIPMENT, outputStack, ingredients){
            // copy nbt tag from helmet to new helmet, also delete SolarHelmet tag
            @Override
            public ItemStack assemble(CraftingContainer container, RegistryAccess access) {
                ItemStack assembled = super.assemble(container, access);
                ItemStack inputStack = ItemStack.EMPTY;
                for (int slotId = 0; slotId < container.getContainerSize(); slotId++) {
                    if(!container.getItem(slotId).isEmpty()){
                        inputStack = container.getItem(slotId).copy();
                        break;
                    }
                }
                if(!inputStack.isEmpty()){
                    if(inputStack.hasTag()){
                        CompoundTag inputTag = inputStack.getTag();
                        inputTag.remove("SolarHelmet");
                        assembled.setTag(inputTag);
                        NBTHelper.removeModule(module.getTagName(), assembled);
                    }
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
                if (!inputStack.hasTag()) {
                    return true;
                }
                return NBTHelper.isModulePresent(module.getTagName(), inputStack);
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
