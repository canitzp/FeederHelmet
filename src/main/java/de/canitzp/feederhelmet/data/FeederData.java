package de.canitzp.feederhelmet.data;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = FeederHelmet.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class FeederData {

    @SubscribeEvent
    public static void runData(GatherDataEvent event){
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        if(event.includeClient()){
            generator.addProvider(true, new FHItemModel(generator, helper));
        }
    }

}
