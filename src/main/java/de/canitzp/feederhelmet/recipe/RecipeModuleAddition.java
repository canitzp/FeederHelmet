package de.canitzp.feederhelmet.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import de.canitzp.feederhelmet.FeederHelmet;
import de.canitzp.feederhelmet.NBTHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RecipeModuleAddition implements SmithingRecipe {

    private final Item helmet;
    private final String module;
    private final ItemStack outputStack;

    public RecipeModuleAddition(Item helmet, String module, ItemStack outputStack) {
        this.helmet = helmet;
        this.module = module;
        this.outputStack = outputStack;
    }

    @Override
    public boolean isTemplateIngredient(ItemStack stack) {
        return stack.isEmpty();
    }

    @Override
    public boolean isBaseIngredient(ItemStack stack) {
        return stack.is(helmet) && !NBTHelper.isModulePresent(module, stack);
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
        NBTHelper.addModule(module, assembled);
        return assembled;
    }

    @Override
    public ItemStack getResultItem(RegistryAccess access) {
        return outputStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FeederHelmet.MODULE_ADDITION_SERIALIZER.get();
    }

    public Item getBase() {
        return this.helmet;
    }

    public ItemStack getOutputStack() {
        return this.outputStack;
    }

    public static class Serializer implements RecipeSerializer<RecipeModuleAddition> {

        @Override
        public @Nullable RecipeModuleAddition fromNetwork(FriendlyByteBuf buffer) {
            Item helmet = BuiltInRegistries.ITEM.get(buffer.readResourceLocation());
            String module = buffer.readUtf();
            ItemStack outputStack = buffer.readItem();
            return new RecipeModuleAddition(helmet, module, outputStack);
        }
    
        @Override
        public @NotNull Codec<RecipeModuleAddition> codec(){
            return RecordCodecBuilder.create(
                codec -> codec.group(
                ResourceLocation.CODEC.fieldOf("helmet").forGetter(recipe -> BuiltInRegistries.ITEM.getKey(recipe.helmet)),
                Codec.STRING.fieldOf("module").forGetter(recipe -> recipe.module),
                ItemStack.CODEC.fieldOf("result").forGetter(recipe -> recipe.outputStack)
            ).apply(codec, (helmetResourceLocation, module, result) -> new RecipeModuleAddition(BuiltInRegistries.ITEM.get(helmetResourceLocation), module, result)));
        }
    
        @Override
        public void toNetwork(FriendlyByteBuf buffer, RecipeModuleAddition recipe) {
            buffer.writeResourceLocation(BuiltInRegistries.ITEM.getKey(recipe.helmet));
            buffer.writeUtf(recipe.module);
            buffer.writeItem(recipe.outputStack);
        }
    }

}
