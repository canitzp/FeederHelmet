package de.canitzp.feederhelmet;

import de.canitzp.feederhelmet.item.ItemFeederModule;
import de.canitzp.feederhelmet.item.ItemPhotosynthesisModule;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.LevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
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
    
    public static final String MODID = "feederhelmet";
    
    private static final Logger LOGGER = LogManager.getLogger(FeederHelmet.MODID);
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<ItemFeederModule> FEEDER_HELMET_MODULE_ITEM = ITEMS.register("feeder_helmet_module", ItemFeederModule::new);
    public static final RegistryObject<ItemPhotosynthesisModule> PHOTOSYNTHESIS_MODULE_ITEM = ITEMS.register("photosynthesis_helmet_module", ItemPhotosynthesisModule::new);
    
    public static final List<IHelmetModule> MODULES = new ArrayList<>();
    
    public FeederHelmet() {
        LOGGER.info("Feeder Helmet loading...");
        MODULES.add(new FeederModule());

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FeederConfig.spec);

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
            if(level.dimension() != Level.OVERWORLD){
                return;
            }
            LOGGER.info("Feeder Helmet recipe injecting...");
            RecipeManager recipeManager = level.getRecipeManager();
    
            // list which the old recipes are replaced with. This should include all existing recipes and the new ones, before recipeManager#replaceRecipes is called!
            List<Recipe<?>> allNewRecipes = new ArrayList<>();
            for(IHelmetModule module : MODULES){
                for(Item helmet : ForgeRegistries.ITEMS.getValues()){
                    if(module.isModuleApplicableTo(helmet.getDefaultInstance())){
                        ResourceLocation helmetKey = ForgeRegistries.ITEMS.getKey(helmet);
                        // create recipe id for creation recipe
                        ResourceLocation creationCraftingId = new ResourceLocation(MODID, module.getTagName() + "_creation_" + helmetKey.getNamespace() + "_" + helmetKey.getPath());
                        // create recipe id for removal recipe
                        ResourceLocation removalCraftingId = new ResourceLocation(MODID, module.getTagName() + "_removal_" + helmetKey.getNamespace() + "_" + helmetKey.getPath());
                        // create recipe for creation
                        Recipe<?> creationRecipe = FeederRecipeManager.creationRecipe(module, helmet, creationCraftingId);
                        // create recipe for removal
                        Recipe<?> removalRecipe = FeederRecipeManager.removalRecipe(module, helmet, removalCraftingId);

                        // add creation recipe to recipes list
                        if(recipeManager.getRecipeIds().noneMatch(resourceLocation -> resourceLocation.equals(creationCraftingId))){
                            allNewRecipes.add(creationRecipe);
                            LOGGER.info(String.format("Feeder Helmet created %s recipe for %s with id '%s'", module.getTagName(), helmetKey, creationCraftingId));
                        }
                        // add removal recipe to recipes list
                        if(recipeManager.getRecipeIds().noneMatch(resourceLocation -> resourceLocation.equals(removalCraftingId))){
                            allNewRecipes.add(removalRecipe);
                            LOGGER.info(String.format("Feeder Helmet created %s recipe for %s with id '%s'", module.getTagName(), helmetKey, removalCraftingId));
                        }
                    }
                }
            }
            
            try{
                // add all existing recipes, since we're gonna replace them
                allNewRecipes.addAll(recipeManager.getRecipes());
                recipeManager.replaceRecipes(allNewRecipes);
            } catch(IllegalStateException e){
                LOGGER.error("Feeder Helmet: Illegal recipe replacement caught! Report this to author immediately!", e);
            }
        }
    }
    
    @SubscribeEvent
    public static void updatePlayer(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END && !event.player.level.isClientSide() && event.player.getCommandSenderWorld().getGameTime() % FeederConfig.GENERAL.WAIT_TICKS.get() == 0){
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
        
        stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(energyCapability -> {
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
