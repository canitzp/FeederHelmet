package de.canitzp.feederhelmet;

import com.electronwill.nightconfig.core.file.FileConfig;

import java.io.File;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FeederHelmetStandaloneConfig {
    
    public static int durability;
    public static int energyConsumption;
    public static boolean canBreak;
    public static int waitTicks;
    public static boolean preventFoodHungerLoss;
    public static final AtomicBoolean IGNORE_WAITING_WHEN_LOW_HEART;
    public static final AtomicBoolean BLACKLIST_SMELTABLES;
    public static final AtomicReference<List<String>> HELMET_BLACKLIST;
    public static final AtomicReference<List<String>> HELMET_WHITELIST;
    public static final AtomicReference<List<String>> FOOD_BLACKLIST;
    public static final AtomicReference<List<String>> FOOD_WHITELIST;
    public static final AtomicBoolean FOOD_WHITELIST_ONLY;
    
    private static FileConfig loadConfigFromFile(){
        File file = new File(".", "config" + File.separator + "feederhelmet.toml");
        return FileConfig.builder(file).defaultResource("assets/data/feederhelmet/default-config.toml").build();
    }
    
    public static void loadConfig(){
        FileConfig conf = FeederHelmetStandaloneConfig.loadConfigFromFile();
        conf.load();
        
        FeederHelmetStandaloneConfig.durability = conf.getInt("general.durability");
        FeederHelmetStandaloneConfig.energyConsumption = conf.getInt("general.energy_consumption");
        FeederHelmetStandaloneConfig.canBreak = conf.get("general.can_break");
        FeederHelmetStandaloneConfig.waitTicks = conf.getInt("general.wait_ticks");
        FeederHelmetStandaloneConfig.preventFoodHungerLoss = conf.get("general.prevent_food_hunger_loss");
    }
    
}
