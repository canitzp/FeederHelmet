package de.canitzp.feederhelmet;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import de.canitzp.feederhelmet.item.ItemFeederModule;
import de.canitzp.feederhelmet.item.ItemPhotosynthesisModule;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ArmorItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
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
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
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
    
    public static final ItemGroup TAB = new ItemGroup(MODID){
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
                        CompoundNBT tag = new CompoundNBT();
                        ListNBT modulesList = new ListNBT();
                        modulesList.add(StringNBT.valueOf(module.getTagName()));
                        tag.put("modules", modulesList);
                        stack.setTag(tag);
                        stacks.add(stack);
                    }
                }
            }
        }
    };
    
    public FeederHelmet(){
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
                    module.renderTooltip(event.getItemStack(), event.getPlayer(), event.getToolTip(), event.getFlags());
                }
            }
        }
    }
    
    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event){
        IWorld levelAccessor = event.getWorld();
        if(levelAccessor.isClientSide()){
            return;
        }
        if(levelAccessor instanceof World){
            World level = (World) levelAccessor;
            if(level.dimension() != World.OVERWORLD){
                return;
            }
            LOGGER.info("Feeder Helmet recipe injecting...");
            RecipeManager recipeManager = level.getRecipeManager();
    
            // list which the old recipes are replaced with. This should include all existing recipes and the new ones, before recipeManager#replaceRecipes is called!
            List<IRecipe<?>> allNewRecipes = new ArrayList<>();
            for(IHelmetModule module : MODULES){
                for(Item helmet : ForgeRegistries.ITEMS.getValues()){
                    if(module.isModuleApplicableTo(helmet.getDefaultInstance())){
                        NonNullList<Ingredient> recipeInputItems = NonNullList.create();
                        recipeInputItems.add(Ingredient.of(module.getCorrespondingModuleItem()));
                        recipeInputItems.add(Ingredient.of(helmet));

                        ItemStack recipeOutputStack = new ItemStack(helmet);

                        ResourceLocation craftingId = new ResourceLocation(MODID, module.getTagName() + "_" + helmet.getRegistryName().getNamespace() + "_" + helmet.getRegistryName().getPath());

                        ShapelessRecipe recipe = new ShapelessRecipe(craftingId, "", recipeOutputStack, recipeInputItems) {
                            @Nonnull
                            @Override
                            public ItemStack assemble(CraftingInventory inv){
                                CompoundNBT nbt = new CompoundNBT();
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
                            public boolean matches(CraftingInventory inv, World worldIn){
                                if(super.matches(inv, worldIn)){
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
                            allNewRecipes.add(recipe);
                            LOGGER.info(String.format("Feeder Helmet created %s recipe for %s with id '%s'", module.getTagName(), helmet.getRegistryName().toString(), craftingId));
                        }
                    }
                }
            }
            
            try{
                // add all existing recipes, since we're gonna replace them
                allNewRecipes.addAll(recipeManager.getRecipes());
                replaceRecipes(recipeManager, allNewRecipes);
            } catch(IllegalStateException e){
                LOGGER.error("Feeder Helmet: Illegal recipe replacement caught! Report this to author immediately!", e);
            }
        }
    }
    
    @SubscribeEvent
    public static void updatePlayer(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END && !event.player.level.isClientSide() && event.player.getCommandSenderWorld().getGameTime() % FeederConfig.GENERAL.WAIT_TICKS.get() == 0){
            ItemStack helmetStack = event.player.inventory.armor.get(EquipmentSlotType.HEAD.getIndex());
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
            CompoundNBT nbt = result.hasTag() ? result.getTag() : new CompoundNBT();
            nbt.put("modules", toRepair.getTag().get("modules"));
            result.setTag(nbt);
        }
    }
    
    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event){
        PlayerEntity player = event.getPlayer();
        NonNullList<ItemStack> armorInventory = player.inventory.armor;
        NonNullList<ItemStack> mainInventory = player.inventory.items;
        NonNullList<ItemStack> offHandInventory = player.inventory.offhand;
        
        NonNullList<ItemStack> mergedInventory = NonNullList.create();
        mergedInventory.addAll(armorInventory);
        mergedInventory.addAll(mainInventory);
        mergedInventory.addAll(offHandInventory);
    
        for(ItemStack stack : mergedInventory){
            if(stack.hasTag()){
                CompoundNBT tag = stack.getTag();
                if(tag.contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                    tag.remove("AutoFeederHelmet");
                    ListNBT modules = tag.getList("modules", Constants.NBT.TAG_STRING);
                    modules.add(StringNBT.valueOf("feeder_module"));
                    tag.put("modules", modules);
                }
            }
        }
    }
    
    public static boolean isItemHelmet(ItemStack stack){
        return (stack.getItem() instanceof ArmorItem && ((ArmorItem) stack.getItem()).getSlot() == EquipmentSlotType.HEAD && !ItemStackUtil.isHelmetBlacklisted(stack)) || ItemStackUtil.isHelmetWhitelisted(stack);
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
    
    private static Field recipes = ObfuscationReflectionHelper.findField(RecipeManager.class, "field_199522_d"); // RecipeManager.recipes
    public static void replaceRecipes(RecipeManager recipeManager, Collection<IRecipe<?>> allNewRecipes){
        if(recipes != null){
            Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map = Maps.newHashMap();
            allNewRecipes.forEach((recipe) -> {
                Map<ResourceLocation, IRecipe<?>> map1 = map.computeIfAbsent(recipe.getType(), (p_223390_0_) -> Maps.newHashMap());
                IRecipe<?> overwrittenRecipe = map1.put(recipe.getId(), recipe);
                if (overwrittenRecipe != null) {
                    throw new IllegalStateException("Duplicate recipe ignored with ID " + recipe.getId());
                }
            });
            try{
                recipes.set(recipeManager, ImmutableMap.copyOf(map));
            } catch(IllegalAccessException e){
                e.printStackTrace();
            }
        }
    }
    
}