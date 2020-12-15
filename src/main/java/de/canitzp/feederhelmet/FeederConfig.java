package de.canitzp.feederhelmet;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.List;

public class FeederConfig {
    
    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final PhotosynthesisModule PHOTOSYNTHESIS_MODULE = new PhotosynthesisModule(BUILDER);
    public static final ForgeConfigSpec spec = BUILDER.build();

    // todo reorganize when breaking change
    public static class General {
        public final ForgeConfigSpec.ConfigValue<Integer> DURABILITY;
        public final ForgeConfigSpec.ConfigValue<Integer> ENERGY_CONSUMPTION;
        public final ForgeConfigSpec.ConfigValue<Integer> WAIT_TICKS;
        public final ForgeConfigSpec.ConfigValue<Boolean> CAN_BREAK;
        public final ForgeConfigSpec.ConfigValue<Boolean> WAIT_UNITL_FILL_ALL_HUNGER;
        public final ForgeConfigSpec.ConfigValue<Boolean> IGNORE_WAITING_WHEN_LOW_HEART;
        public final ForgeConfigSpec.ConfigValue<List<String>> HELMET_BLACKLIST;
        public final ForgeConfigSpec.ConfigValue<List<String>> HELMET_WHITELIST;
        public final ForgeConfigSpec.ConfigValue<List<String>> FOOD_BLACKLIST;
        public final ForgeConfigSpec.ConfigValue<List<String>> FOOD_WHITELIST;
    
        public General(ForgeConfigSpec.Builder builder) {
            builder.push("General");
            DURABILITY = builder
                .comment("How much durability should the helmet use for every food eaten, when it is a unpowered helmet.")
                .translation("Durability necessary")
                .defineInRange("durability_consumption", 1, 0, 64);
            ENERGY_CONSUMPTION = builder
                .comment("How much energy should the helmet use for every food eaten, when it is a powered helmet.")
                .translation("Energy necessary")
                .defineInRange("energy_consumption", 10, 0, 5000);
            CAN_BREAK = builder
                .comment("Can the helmet break while feeding? If this is false, the helmet stops feeding you when durability to low. Only when the helmet isn't powered by Energy.")
                .translation("Can break helmet")
                .define("can_helmet_break", false);
            WAIT_TICKS = builder
                .comment("Defines how much ticks are between food checks. 20 ticks = 1 second")
                .translation("Ticks between inventory scan")
                .defineInRange("feed_ticks", 20, 1, 200);
            WAIT_UNITL_FILL_ALL_HUNGER = builder
                .comment("Should the helmet wait until the food can be eaten completely, without any hunger loss by early eating?")
                .translation("Wait until hungry enough")
                .define("hungry_enough_wait", true);
            IGNORE_WAITING_WHEN_LOW_HEART = builder
                .comment("Should the 'hungry_enough_wait' option be ignored when the player is low on hearts? (less or equal 50%)")
                .translation("Ignore hunger wait when on low hearts")
                .define("ignore_hungry_enough_wait_when_heart_low", true);
            HELMET_BLACKLIST = builder
                .comment("The here stated items can't be used as FeederHelmet")
                .translation("Helmet blacklist")
                .worldRestart()
                .define("helmet_blacklist", new ArrayList<>());
            HELMET_WHITELIST = builder
                .comment("The here stated items can be used as Feeder Helmet, even when they aren't helmets at all (You can't put everything in you helmet slot)")
                .translation("Helmet Whitelist")
                .worldRestart()
                .define("helmet_whitelist", new ArrayList<>());
            FOOD_BLACKLIST = builder
                .comment("All here stated items aren't consumable by the FeederHelmet")
                .translation("Food blacklist")
                .worldRestart()
                .define("food_blacklist", new ArrayList<>());
            FOOD_WHITELIST = builder
                .comment("All here stated items are additionally to all default items eatable. This can be very dangerous, because it is possible that the helmet doesn't eat it, but uses it!!!")
                .translation("Food whitelist")
                .worldRestart()
                .define("food_whitelist", new ArrayList<>());
            builder.pop();
        }
    }
    
    public static class PhotosynthesisModule {
        public PhotosynthesisModule(ForgeConfigSpec.Builder builder) {
        
        }
    }
    
}