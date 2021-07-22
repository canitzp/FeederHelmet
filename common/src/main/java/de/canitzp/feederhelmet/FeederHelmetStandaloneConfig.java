package de.canitzp.feederhelmet;

import com.electronwill.nightconfig.core.file.FileConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.network.chat.TranslatableComponent;
import org.tomlj.Toml;
import org.tomlj.TomlParseResult;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

public class FeederHelmetStandaloneConfig {
    
    public static int durability;
    public static final AtomicInteger ENERGY_CONSUMPTION;
    public static final AtomicInteger WAIT_TICKS;
    public static final AtomicBoolean CAN_BREAK;
    public static final AtomicBoolean WAIT_UNITL_FILL_ALL_HUNGER;
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
        
        FeederHelmetStandaloneConfig.durability = conf.getInt("general.durability");
    }
    
}
