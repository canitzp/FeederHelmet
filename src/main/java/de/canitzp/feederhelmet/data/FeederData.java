package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import de.canitzp.feederhelmet.data.localization.FHLocalizationUSEnglish;
import net.minecraft.data.DataGenerator;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod.EventBusSubscriber(modid = FeederHelmet.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeederData {

    @SubscribeEvent
    public static void runData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        generator.addProvider(event.includeClient(), new FHItemModel(generator, helper));
        generator.addProvider(event.includeClient(), new FHLocalizationUSEnglish(generator.getPackOutput()));

        generator.addProvider(event.includeServer(), new FHRecipeProvider(generator.getPackOutput()));
    }

}
