package de.canitzp.feederhelmet;

import de.canitzp.feederhelmet.item.ItemFeederModule;
import de.canitzp.feederhelmet.item.ItemPhotosynthesisModule;
import de.canitzp.feederhelmet.module.FeederModule;
import de.canitzp.feederhelmet.module.IHelmetModule;
import de.canitzp.feederhelmet.module.PhotosynthesisModule;
import de.canitzp.feederhelmet.recipe.FeederRecipeManager;
import de.canitzp.feederhelmet.recipe.RecipeModuleAddition;
import net.minecraft.core.NonNullList;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import net.neoforged.neoforge.common.capabilities.Capabilities;
import net.neoforged.neoforge.event.TickEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author canitzp
 */
@Mod.EventBusSubscriber
@Mod(FeederHelmet.MODID)
public class FeederHelmet{
    
    public static final java.lang.String MODID = "feederhelmet";
    
    public static final Logger LOGGER = LogManager.getLogger(FeederHelmet.MODID);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final RegistryObject<CreativeModeTab> TAB = TABS.register("tab", FeederTab::create);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final RegistryObject<RecipeSerializer<RecipeModuleAddition>> MODULE_ADDITION_SERIALIZER = RECIPE_SERIALIZER.register("module_addition", RecipeModuleAddition.Serializer::new);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<ItemFeederModule> FEEDER_HELMET_MODULE_ITEM = ITEMS.register("feeder_helmet_module", ItemFeederModule::new);
    public static final RegistryObject<ItemPhotosynthesisModule> PHOTOSYNTHESIS_MODULE_ITEM = ITEMS.register("photosynthesis_helmet_module", ItemPhotosynthesisModule::new);
    
    public static final List<IHelmetModule> MODULES = new ArrayList<>();
    
    public FeederHelmet() {
        LOGGER.info("Feeder Helmet loading...");
        MODULES.add(new FeederModule());
        MODULES.add(new PhotosynthesisModule());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FeederConfig.spec);

        TABS.register(FMLJavaModLoadingContext.get().getModEventBus());
        RECIPE_SERIALIZER.register(FMLJavaModLoadingContext.get().getModEventBus());
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        LOGGER.info("Feeder Helmet loaded.");
    }
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderTooltips(ItemTooltipEvent event){
        if(!event.getItemStack().isEmpty()){
            for(IHelmetModule module : MODULES){
                if(NBTHelper.isModulePresent(module.getTagName(), event.getItemStack())){
                    module.renderTooltip(event.getItemStack(), event.getEntity(), event.getToolTip(), event.getFlags());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(LevelEvent.Load event){
        LevelAccessor levelAccessor = event.getLevel();
        if(levelAccessor.isClientSide()){
            return;
        }
        if(levelAccessor instanceof Level level){
            FeederRecipeManager.injectRecipes(level);
        }
    }
    
    @SubscribeEvent
    public static void updatePlayer(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END && !event.player.level().isClientSide() && event.player.getCommandSenderWorld().getGameTime() % FeederConfig.GENERAL.WAIT_TICKS.get() == 0){
            ItemStack helmetStack = event.player.getInventory().armor.get(EquipmentSlot.HEAD.getIndex());
            for(IHelmetModule module : MODULES){
                if(NBTHelper.isModulePresent(module.getTagName(), helmetStack)){
                    module.updatePlayer(event.player, helmetStack);
                }
            }
        }
    }
    
    // copy modules nbt from old to ew item stack
    @SubscribeEvent
    public static void anvilRepair(AnvilRepairEvent event){
        ItemStack toRepair = event.getLeft();
        ItemStack result = event.getOutput();
        
        if(toRepair.hasTag() && toRepair.getTag().contains("modules", Tag.TAG_LIST)){
            CompoundTag nbt = result.hasTag() ? result.getTag() : new CompoundTag();
            nbt.put("modules", toRepair.getTag().get("modules"));
            result.setTag(nbt);
        }
    }
    
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        NonNullList<ItemStack> armorInventory = player.getInventory().armor;
        NonNullList<ItemStack> mainInventory = player.getInventory().items;
        NonNullList<ItemStack> offHandInventory = player.getInventory().offhand;
        
        NonNullList<ItemStack> mergedInventory = NonNullList.create();
        mergedInventory.addAll(armorInventory);
        mergedInventory.addAll(mainInventory);
        mergedInventory.addAll(offHandInventory);
    
        for(ItemStack stack : mergedInventory){
            if(stack.hasTag()){
                CompoundTag tag = stack.getTag();
                if(tag.contains("AutoFeederHelmet", Tag.TAG_BYTE)){
                    tag.remove("AutoFeederHelmet");
                    ListTag modules = tag.getList("modules", Tag.TAG_STRING);
                    modules.add(StringTag.valueOf("feeder_module"));
                    tag.put("modules", modules);
                }
            }
        }
    }

    public static boolean isItemHelmet(ItemStack stack){
        return (stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getType().getSlot() == EquipmentSlot.HEAD && !ItemStackUtil.isHelmetBlacklisted(stack)) || ItemStackUtil.isHelmetWhitelisted(stack);
    }
    
    public static boolean canDamageBeReducedOrEnergyConsumed(@Nonnull ItemStack stack){
        AtomicBoolean canWork = new AtomicBoolean(false);
        
        stack.getCapability(Capabilities.ENERGY).ifPresent(energyCapability -> {
            canWork.set(true);
        });
        
        if(!canWork.get()){
            if(stack.isDamageableItem()){
                int newDmg = stack.getDamageValue() + FeederConfig.GENERAL.DURABILITY.get();
                if(FeederConfig.GENERAL.CAN_BREAK.get()){
                    canWork.set(newDmg <= stack.getMaxDamage());
                } else {
                    canWork.set(newDmg < stack.getMaxDamage());
                }
            } else {
                // There are super-op helmets that aren't damageable, so we need to account for that (eg: Wyvern Armor by Draconic Evolution)
                canWork.set(true);
            }
        }
        
        return canWork.get();
    }

}
