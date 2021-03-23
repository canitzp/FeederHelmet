package de.canitzp.feederhelmet;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber(modid = FeederHelmet.MODID)
@Config.LangKey("config." + FeederHelmet.MODID + ":config.name")
@Config(modid = FeederHelmet.MODID)
public class FeederConfig {

    @SubscribeEvent
    public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event){
        if(FeederHelmet.MODID.equals(event.getModID())){
            ConfigManager.sync(event.getModID(), Config.Type.INSTANCE);
        }
    }

    @Config.Comment("How much durability should the helmet use for every food eaten. Unpowered helmets")
    @Config.Name("Durability necessary")
    @Config.RangeInt(min = 0, max = 64)
    public static int DURABILITY = 1;
    
    @Config.Comment("How much energy should the helmet use for every food eaten, when it is a powered helmet.")
    @Config.Name("Energy necessary")
    @Config.RangeInt(min = 0, max = 5000)
    public static int ENERGY_CONSUMPTION = 10;

    @Config.Comment("Can the helmet break while feeding? If this is false, the helmet stops feeding you when durability to low. Only when the helmet isn't powered by Energy.")
    @Config.Name("Can break helmet")
    public static boolean CAN_BREAK_HELMET = false;

    @Config.Comment("Uses more end game items to craft the feeder module.")
    @Config.Name("Hard module recipe")
    @Config.RequiresMcRestart
    public static boolean HARD_MODULE_RECIPE = false;

    @Config.Comment("Defines how much ticks are between food checks. 20 ticks = 1 second")
    @Config.Name("Ticks between inventory scan")
    public static int WAIT_TICKS = 20;

    @Config.Comment("Put additional items to craft a helmet with a module in here. Up to 7")
    @Config.Name("Additional crafting items")
    @Config.RequiresMcRestart
    public static String[] ADD_CRAFT_ITEMS = new String[0];

    @Config.Comment("The here stated items can't be used as FeederHelmet")
    @Config.Name("Helmet blacklist")
    @Config.RequiresMcRestart
    public static String[] HELMET_BLACKLIST = new String[0];

    @Config.Comment("The here stated items can be used as Feeder Helmet, even when they aren't helmets at all (You can't put everything in you helmet slot)")
    @Config.Name("Helmet Whitelist")
    @Config.RequiresMcRestart
    public static String[] HELMET_WHITELIST = new String[0];

    @Config.Comment("All here stated items aren't consumable by the FeederHelmet")
    @Config.Name("Food blacklist")
    public static String[] FOOD_BLACKLIST = new String[0];

    @Config.Comment("All here stated items are additionally to all default items eatable. This can be very dangerous, because it is possible that the helmet doesn't eat it, but uses it!!!")
    @Config.Name("Food whitelist")
    public static String[] FOOD_WHITELIST = new String[0];

    @Config.Comment("Set this to true to ignore all eatable items and only use food listed in the whitelist.")
    @Config.Name("Food whitelist only")
    public static boolean FOOD_ONLY_WHITELIST = false;

}
