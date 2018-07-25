package de.canitzp.feederhelmet;

import net.minecraftforge.common.config.Config;

import java.util.ArrayList;
import java.util.List;

@Config.LangKey("config." + FeederHelmet.MODID + ":config.name")
@Config(modid = FeederHelmet.MODID)
public class FeederConfig {

    @Config.Comment("How much durability should the helmet use for every food eaten.")
    @Config.Name("Durability necessary")
    @Config.RangeInt(min = 0, max = 64)
    public static int DURABILITY = 1;

    @Config.Comment("Can the helmet break while feeding? If this is false, the helmet stops feeding you when durability to low.")
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

}
