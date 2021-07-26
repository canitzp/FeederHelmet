package de.canitzp.feederhelmet;

import com.google.common.collect.ImmutableMap;
import de.canitzp.feederhelmet.item.ItemFeederModule;
import de.canitzp.feederhelmet.item.ItemPhotosynthesisModule;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fmllegacy.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

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
    
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final RegistryObject<ItemFeederModule> FEEDER_HELMET_MODULE_ITEM = ITEMS.register("feeder_helmet_module", ItemFeederModule::new);
    public static final RegistryObject<ItemPhotosynthesisModule> PHOTOSYNTHESIS_MODULE_ITEM = ITEMS.register("photosynthesis_helmet_module", ItemPhotosynthesisModule::new);
    
    public static final List<IHelmetModule> MODULES = new ArrayList<>();
    
    public static final CreativeModeTab TAB = new CreativeModeTab(MODID){
        @Override
        public ItemStack makeIcon(){
            return new ItemStack(FEEDER_HELMET_MODULE_ITEM.get());
        }

        @OnlyIn(Dist.CLIENT)
        @Override
        public void fillItemList(NonNullList<ItemStack> stacks){
            for(IHelmetModule module : MODULES){
                stacks.add(new ItemStack(module.getCorrespondingModuleItem()));
                for(Item item : ForgeRegistries.ITEMS){
                    if(module.isModuleApplicableTo(item.getDefaultInstance())){
                        ItemStack stack = new ItemStack(item);
                        CompoundTag tag = new CompoundTag();
                        ListTag modulesList = new ListTag();
                        modulesList.add(StringTag.valueOf(module.getTagName()));
                        tag.put("modules", modulesList);
                        stack.setTag(tag);
                        stacks.add(stack);
                    }
                }
            }
        }
    };
    
    public FeederHelmet(){
        MODULES.add(new FeederModule());
        
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FeederConfig.spec);
        
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderTooltips(ItemTooltipEvent event){
        if(!event.getItemStack().isEmpty()){
            for(IHelmetModule module : MODULES){
                if(NBTHelper.isModulePresent(module.getTagName(), event.getItemStack())){
                    module.renderTooltip(event.getItemStack(), event.getPlayer(), event.getToolTip(), event.getFlags());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event){
        LevelAccessor levelAccessor = event.getWorld();
        if(levelAccessor instanceof Level level){
            if(level.dimension() != Level.OVERWORLD){
                return;
            }
            RecipeManager recipeManager = level.getRecipeManager();

            List<Recipe<?>> moduleRecipes = new ArrayList<>();
            for(IHelmetModule module : MODULES){
                for(Item helmet : ForgeRegistries.ITEMS.getValues()){
                    if(module.isModuleApplicableTo(helmet.getDefaultInstance())){
                        NonNullList<Ingredient> recipeInputItems = NonNullList.create();
                        recipeInputItems.add(Ingredient.of(module.getCorrespondingModuleItem()));
                        recipeInputItems.add(Ingredient.of(helmet));

                        ItemStack recipeOutputStack = new ItemStack(helmet);

                        ResourceLocation craftingId = new ResourceLocation(MODID, module.getTagName() + "_" + helmet.getRegistryName().getPath());

                        ShapelessRecipe recipe = new ShapelessRecipe(craftingId, "", recipeOutputStack, recipeInputItems) {
                            @Nonnull
                            @Override
                            public ItemStack assemble(CraftingContainer inv){
                                CompoundTag nbt = new CompoundTag();
                                for(int i = 0; i < inv.getContainerSize(); i++){
                                    ItemStack stack = inv.getItem(i);
                                    if(!stack.isEmpty() && stack.getItem() instanceof ArmorItem){
                                        if(stack.hasTag()){
                                            nbt = stack.getTag().copy();
                                        }
                                    }
                                }
                                ItemStack out = super.assemble(inv);
                                out.setTag(nbt);
                                NBTHelper.addModule(module.getTagName(), out);
                                return out;
                            }

                            // checks if the helmet doesn't already have the module
                            @Override
                            public boolean matches(CraftingContainer inv, Level level1){
                                if(super.matches(inv, level1)){
                                    for(int i = 0; i < inv.getContainerSize(); i++){
                                        ItemStack stack = inv.getItem(i);
                                        if(!stack.isEmpty() && stack.getItem() instanceof ArmorItem){
                                            if(NBTHelper.isModulePresent(module.getTagName(), stack)){
                                                return false;
                                            }
                                        }
                                    }
                                    return true;
                                }
                                return false;
                            }
                        };
                        
                        if(recipeManager.getRecipeIds().noneMatch(resourceLocation -> resourceLocation.equals(craftingId))){
                            moduleRecipes.add(recipe);
                            System.out.printf("Registering recipes for module: '%s'; recipe id: '%s'%n", module.getTagName(), craftingId);
                        }
                    }
                }
            }
            
            recipeManager.replaceRecipes(moduleRecipes);
        }
    }
    
    @SubscribeEvent
    public static void updatePlayer(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END && event.player.getCommandSenderWorld().getGameTime() % FeederConfig.GENERAL.WAIT_TICKS.get() == 0){
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
        ItemStack toRepair = event.getItemInput();
        ItemStack result = event.getItemResult();
        
        if(toRepair.hasTag() && toRepair.getTag().contains("modules", Constants.NBT.TAG_LIST)){
            CompoundTag nbt = result.hasTag() ? result.getTag() : new CompoundTag();
            nbt.put("modules", toRepair.getTag().get("modules"));
            result.setTag(nbt);
        }
    }
    
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getPlayer();
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
                if(tag.contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                    tag.remove("AutoFeederHelmet");
                    ListTag modules = tag.getList("modules", Constants.NBT.TAG_STRING);
                    modules.add(StringTag.valueOf("feeder_module"));
                    tag.put("modules", modules);
                }
            }
        }
    }
    
    public static boolean isItemHelmet(ItemStack stack){
        return (stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlot() == EquipmentSlot.HEAD && !ItemStackUtil.isHelmetBlacklisted(stack)) || ItemStackUtil.isHelmetWhitelisted(stack);
    }
    
    public static boolean canDamageBeReducedOrEnergyConsumed(@Nonnull ItemStack stack){
        AtomicBoolean canWork = new AtomicBoolean(false);
        
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energyCapability -> {
            if (stack.hasTag()) {
                int energy = stack.getTag().getInt("Energy");
                canWork.set(energy >= FeederConfig.GENERAL.ENERGY_CONSUMPTION.get());
            }
        });
        
        if(!canWork.get()){
            if(stack.isDamageableItem()){
                int newDmg = stack.getDamageValue() + FeederConfig.GENERAL.DURABILITY.get();
                canWork.set(newDmg < stack.getMaxDamage() || FeederConfig.GENERAL.CAN_BREAK.get());
            } else {
                // There are super-op helmets that aren't damageable, so we need to account for that (eg: Wyvern Armor by Draconic Evolution)
                canWork.set(true);
            }
        }
        
        return canWork.get();
    }
    
}