package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.*;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.common.Tags;
import org.jetbrains.annotations.NotNull;


public class FHRecipeProvider extends RecipeProvider {

    public FHRecipeProvider(PackOutput output) {
        super(output);
    }

    @Override
    protected void buildRecipes(@NotNull RecipeOutput writer) {
        ShapedRecipeBuilder.shaped(RecipeCategory.TOOLS, FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get())
                .define('s', Items.STICK)
                .define('b', Items.BOWL)
                .define('i', Tags.Items.INGOTS_IRON)
                .pattern(" s ")
                .pattern("sbs")
                .pattern("iii")
                .unlockedBy("has_iron", has(Tags.Items.INGOTS_IRON))
                .save(writer);
    }
}
