package de.canitzp.feederhelmet;

import com.google.common.collect.Lists;
import net.neoforged.neoforge.common.ModConfigSpec;
import net.neoforged.neoforge.common.NeoForgeConfig;

import java.util.ArrayList;
import java.util.List;

public class FeederConfig {
    
    private static final ModConfigSpec.Builder BUILDER = new ModConfigSpec.Builder();
    public static final General GENERAL = new General(BUILDER);
    public static final PhotosynthesisModule PHOTOSYNTHESIS_MODULE = new PhotosynthesisModule(BUILDER);
    public static final ModConfigSpec spec = BUILDER.build();

    // todo reorganize when breaking change
    public static class General {
        public final ModConfigSpec.ConfigValue<Integer> DURABILITY;
        public final ModConfigSpec.ConfigValue<Integer> ENERGY_CONSUMPTION;
        public final ModConfigSpec.ConfigValue<Integer> WAIT_TICKS;
        public final ModConfigSpec.ConfigValue<Boolean> CAN_BREAK;
        public final ModConfigSpec.ConfigValue<Boolean> WAIT_UNITL_FILL_ALL_HUNGER;
        public final ModConfigSpec.ConfigValue<Boolean> IGNORE_WAITING_WHEN_LOW_HEART;
        public final ModConfigSpec.ConfigValue<Boolean> BLACKLIST_SMELTABLES;
        public final ModConfigSpec.ConfigValue<List<String>> HELMET_BLACKLIST;
        public final ModConfigSpec.ConfigValue<List<String>> HELMET_WHITELIST;
        public final ModConfigSpec.ConfigValue<List<String>> FOOD_BLACKLIST;
        public final ModConfigSpec.ConfigValue<List<String>> FOOD_WHITELIST;
        public final ModConfigSpec.ConfigValue<Boolean> FOOD_WHITELIST_ONLY;
    
        public General(ModConfigSpec.Builder builder) {
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
            BLACKLIST_SMELTABLES = builder
                .comment("If enabled all eatable items, that also can be smelted are blacklisted.")
                .translation("Blacklist smeltable foods")
                .define("blacklist_smeltables", false);
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
                .define("food_blacklist", Lists.newArrayList("minecraft:rotten_flesh", "minecraft:spider_eye", "minecraft:porkchop", "minecraft:beef", "minecraft:mutton", "minecraft:salmon", "minecraft:chicken", "minecraft:rabbit", "minecraft:potato", "minecraft:chorus_fruit", "minecraft:pufferfish", "minecraft:poisonous_potato"));
            FOOD_WHITELIST = builder
                .comment("All here stated items are additionally to all default items eatable. This can be very dangerous, because it is possible that the helmet doesn't eat it, but uses it!!!")
                .translation("Food whitelist")
                .worldRestart()
                .define("food_whitelist", new ArrayList<>());
            FOOD_WHITELIST_ONLY = builder
                    .comment("Set this to true to ignore all eatable items and only use food listed in the whitelist.")
                    .translation("Food whitelist only")
                    .worldRestart()
                    .define("food_whitelist_only", false);
            builder.pop();
        }
    }
    
    public static class PhotosynthesisModule {
        public PhotosynthesisModule(ModConfigSpec.Builder builder) {
        
        }
    }
    
}