package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.client.data.models.BlockModelGenerators;
import net.minecraft.client.data.models.ItemModelGenerators;
import net.minecraft.client.data.models.ModelProvider;
import net.minecraft.client.data.models.model.ModelTemplates;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;

import java.util.concurrent.CompletableFuture;

public class FHItemModel extends ModelProvider {

    public FHItemModel(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, FeederHelmet.MODID);
    }

    @Override
    protected void registerModels(BlockModelGenerators blockModels, ItemModelGenerators itemModels) {
        itemModels.generateFlatItem(FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get(), ModelTemplates.FLAT_ITEM);
        itemModels.generateFlatItem(FeederHelmet.PHOTOSYNTHESIS_MODULE_ITEM.get(), ModelTemplates.FLAT_ITEM);
        //super.registerModels(blockModels, itemModels);
    }

}
