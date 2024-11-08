package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import net.neoforged.neoforge.common.data.internal.NeoForgeRecipeProvider;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;


public class FHRecipeProvider extends RecipeProvider {

    public FHRecipeProvider(HolderLookup.Provider provider, RecipeOutput output) {
        super(provider, output);
    }

    @Override
    protected void buildRecipes() {
        ShapedRecipeBuilder.shaped(BuiltInRegistries.ITEM, RecipeCategory.TOOLS, FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get())
                .define('s', Items.STICK)
                .define('b', Items.BOWL)
                .define('i', Tags.Items.INGOTS_IRON)
                .pattern(" s ")
                .pattern("sbs")
                .pattern("iii")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(super.output);
    }
}
