package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.DataGenerator;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.client.model.generators.ItemModelProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public class FHItemModel extends ItemModelProvider{

    public FHItemModel(DataGenerator generator, ExistingFileHelper existingFileHelper) {
        super(generator.getPackOutput(), FeederHelmet.MODID, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        singleTexture(FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get());
        singleTexture(FeederHelmet.PHOTOSYNTHESIS_MODULE_ITEM.get());
    }

    private void singleTexture(Item item){
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        singleTexture(key.getPath(), mcLoc("item/handheld"), "layer0", modLoc("item/" + key.getPath()));
    }
}
