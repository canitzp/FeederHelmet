package de.canitzp.feederhelmet.recipe;

import com.google.gson.JsonObject;
import de.canitzp.feederhelmet.FeederHelmet;
import de.canitzp.feederhelmet.NBTHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.Container;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.item.crafting.SmithingRecipe;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class RecipeModuleAddition implements SmithingRecipe {

    private final Item helmet;
    private final String module;
    private final ResourceLocation craftingId;
    private final ItemStack outputStack;

    public RecipeModuleAddition(Item helmet, String module, ResourceLocation craftingId, ItemStack outputStack) {
        this.helmet = helmet;
        this.module = module;
        this.craftingId = craftingId;
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
    public ResourceLocation getId() {
        return craftingId;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return FeederHelmet.MODULE_ADDITION_SERIALIZER.get();
    }

    public static class Serializer implements RecipeSerializer<RecipeModuleAddition> {

        @Override
        public RecipeModuleAddition fromJson(ResourceLocation craftingId, JsonObject json) {
            Item helmet = ForgeRegistries.ITEMS.getValue(new ResourceLocation(GsonHelper.getNonNull(json, "helmet").getAsString()));
            String module = GsonHelper.getNonNull(json, "module").getAsString();
            ItemStack output = ShapedRecipe.itemStackFromJson(GsonHelper.getAsJsonObject(json, "result"));
            return new RecipeModuleAddition(helmet, module, craftingId, output);
        }

        @Override
        public @Nullable RecipeModuleAddition fromNetwork(ResourceLocation craftingId, FriendlyByteBuf buffer) {
            Item helmet = ForgeRegistries.ITEMS.getValue(buffer.readResourceLocation());
            String module = buffer.readUtf();
            ItemStack outputStack = buffer.readItem();
            return new RecipeModuleAddition(helmet, module, craftingId, outputStack);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, RecipeModuleAddition recipe) {
            buffer.writeResourceLocation(ForgeRegistries.ITEMS.getKey(recipe.helmet));
            buffer.writeUtf(recipe.module);
            buffer.writeItemStack(recipe.outputStack, false);
        }
    }

}
