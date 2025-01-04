package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
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
        this.shaped(RecipeCategory.TOOLS, FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get())
                .define('s', Tags.Items.RODS_WOODEN)
                .define('b', Items.BOWL)
                .define('i', Tags.Items.INGOTS_IRON)
                .pattern(" s ")
                .pattern("sbs")
                .pattern("iii")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(super.output);
    }

    public static final class Runner extends RecipeProvider.Runner {
        public Runner(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(output, lookupProvider);
        }

        @Override
        protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
            return new FHRecipeProvider(lookupProvider, output);
        }

        @Override
        public String getName() {
            return "AutoFeederHelmet recipes";
        }
    }
}
