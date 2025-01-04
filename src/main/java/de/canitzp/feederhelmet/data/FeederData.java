package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import de.canitzp.feederhelmet.data.localization.FHLocalizationUSEnglish;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;


@EventBusSubscriber(modid = FeederHelmet.MODID, bus = EventBusSubscriber.Bus.MOD)
public class FeederData {

    @SubscribeEvent
    public static void runData(GatherDataEvent.Client event){
        event.createProvider(FHItemModel::new);
        event.createProvider(FHLocalizationUSEnglish::new);
        event.createProvider(FHRecipeProvider.Runner::new);
    }

}
