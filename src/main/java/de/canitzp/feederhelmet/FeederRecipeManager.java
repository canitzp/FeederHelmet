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

        return new SmithingRecipe() {
            @Override
            public boolean isTemplateIngredient(ItemStack stack) {
                return stack.isEmpty();
            }

            @Override
            public boolean isBaseIngredient(ItemStack stack) {
                return stack.is(helmet) && !NBTHelper.isModulePresent(module.getTagName(), stack);
            }

            @Override
            public boolean isAdditionIngredient(ItemStack stack) {
                return stack.is(FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get());
            }

            @Override
            public boolean matches(Container container, Level level) {
                return isTemplateIngredient(container.getItem(0)) && isBaseIngredient(container.getItem(1)) && isAdditionIngredient(container.getItem(2));
            }

            @Override
            public ItemStack assemble(Container container, RegistryAccess access) {
                ItemStack assembled = this.getResultItem(access).copy();
                // copy old nbt to new stack
                assembled.getOrCreateTag().merge(container.getItem(1).getOrCreateTag());
                // set module flag
                NBTHelper.addModule(module.getTagName(), assembled);
                return assembled;
            }

            @Override
            public ItemStack getResultItem(RegistryAccess access) {
                return outputStack;
            }

            @Override
            public ResourceLocation getId() {
                return craftingId;
            }

            @Override
            public RecipeSerializer<?> getSerializer() {
                return RecipeSerializer.SMITHING_TRANSFORM;
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
                    return false;
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
