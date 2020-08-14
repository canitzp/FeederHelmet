package de.canitzp.feederhelmet;

import com.google.common.collect.ImmutableMap;
import net.minecraft.client.resources.I18n;
import net.minecraft.command.arguments.NBTCompoundTagArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
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
    
    public static final ItemFeederModule feederModule = new ItemFeederModule();
    
    public static final ItemGroup TAB = new ItemGroup(MODID){
        @Override
        public ItemStack createIcon(){
            return new ItemStack(feederModule);
        }
    
        @Override
        public void fill(NonNullList<ItemStack> stacks){
            stacks.add(new ItemStack(feederModule));
            for(Item item : ForgeRegistries.ITEMS){
                if(FeederHelmet.isItemHelmet(item)){
                    ItemStack stack = new ItemStack(item);
                    CompoundNBT tag = new CompoundNBT();
                    tag.putBoolean("AutoFeederHelmet", true);
                    stack.setTag(tag);
                    stacks.add(stack);
                }
            }
        }
    };
    
    public FeederHelmet(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FeederConfig.spec);
        
        ITEMS.register("feeder_helmet_module", () -> feederModule);
        
        ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
    }
    
    @Mod.EventBusSubscriber
    public static class ForgeEvents{
    
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void renderTooltips(ItemTooltipEvent event){
            if(!event.getItemStack().isEmpty()){
                CompoundNBT nbt = event.getItemStack().getTag();
                if(nbt != null && nbt.contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                    event.getToolTip().add(new StringTextComponent(TextFormatting.YELLOW.toString() + TextFormatting.ITALIC.toString() + I18n.format("item.feederhelmet:feeder_helmet_module_installed.text") + TextFormatting.RESET.toString()));
                }
            }
        }
        
        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event){
            IWorld iWorld = event.getWorld();
            if(iWorld instanceof World){
                World world = (World) iWorld;
                RecipeManager recipeManager = world.getRecipeManager();
                NonNullList<Ingredient> ingredients = NonNullList.create();
                ingredients.add(Ingredient.fromItems(feederModule));
                List<String> add_craft_items = FeederConfig.GENERAL.ADD_CRAFT_ITEMS.get();
                add_craft_items.stream()
                               .limit(7)
                               .map(s -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)))
                               .filter(Objects::nonNull)
                               .map(Ingredient::fromItems)
                               .forEach(ingredients::add);
                Map<ResourceLocation, IRecipe<?>> recipesToInject = new HashMap<>();
                ForgeRegistries.ITEMS.getValues().stream()
                                     .filter(FeederHelmet::isItemHelmet)
                                     .forEach(helmet -> {
                                         NonNullList<Ingredient> ingredientsCopy = NonNullList.create();
                                         ingredientsCopy.addAll(ingredients);
                                         ingredientsCopy.add(Ingredient.fromItems(helmet));
                                         ItemStack helmetStack = new ItemStack(helmet);
                                         CompoundNBT nbt = new CompoundNBT();
                                         nbt.putBoolean("AutoFeederHelmet", true);
                                         helmetStack.setTag(nbt);
                                         ResourceLocation craftingId = new ResourceLocation(MODID, "feederhelmet_" + helmet.getRegistryName().getPath());
                                         ShapelessRecipe recipe = new ShapelessRecipe(craftingId, "", helmetStack, ingredientsCopy){
                                             @Nonnull
                                             @Override
                                             public ItemStack getCraftingResult(CraftingInventory inv){
                                                 CompoundNBT nbt = new CompoundNBT();
                                                 for(int i = 0; i < inv.getSizeInventory(); i++){
                                                     ItemStack stack = inv.getStackInSlot(i);
                                                     if(!stack.isEmpty() && stack.getItem() instanceof ArmorItem){
                                                         if(stack.hasTag()){
                                                             nbt = stack.getTag();
                                                         }
                                                     }
                                                 }
                                                 ItemStack out = super.getCraftingResult(inv);
                                                 nbt.putBoolean("AutoFeederHelmet", true);
                                                 out.setTag(nbt);
                                                 return out;
                                             }
            
                                             @Override
                                             public boolean matches(CraftingInventory inv, World worldIn){
                                                 if(super.matches(inv, worldIn)){
                                                     for(int i = 0; i < inv.getSizeInventory(); i++){
                                                         ItemStack stack = inv.getStackInSlot(i);
                                                         if(!stack.isEmpty() && stack.getItem() instanceof ArmorItem){
                                                             CompoundNBT nbt = stack.getTag();
                                                             if(nbt != null && nbt.contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                                                                 return false;
                                                             }
                                                         }
                                                     }
                                                 }
                                                 return super.matches(inv, worldIn);
                                             }
                                         };
                                         if(recipeManager.getKeys().noneMatch(resourceLocation -> resourceLocation.equals(craftingId))){
                                             recipesToInject.put(craftingId, recipe);
                                         }
                                     });
                Map<IRecipeType<?>, Map<ResourceLocation, IRecipe<?>>> map = new HashMap<>(recipeManager.recipes);
                Map<ResourceLocation, IRecipe<?>> craftingRecipes = new HashMap<>(map.getOrDefault(IRecipeType.CRAFTING, Collections.emptyMap()));
                craftingRecipes.putAll(recipesToInject);
                map.put(IRecipeType.CRAFTING, ImmutableMap.copyOf(craftingRecipes));
                recipeManager.recipes = ImmutableMap.copyOf(map);
            }
            
        }
    
        @SubscribeEvent
        public static void updatePlayer(TickEvent.PlayerTickEvent event){
            if(event.phase == TickEvent.Phase.END && event.player.getEntityWorld().getGameTime() % FeederConfig.GENERAL.WAIT_TICKS.get() == 0){
                PlayerInventory inv = event.player.inventory;
                ItemStack helmet = inv.armorInventory.get(EquipmentSlotType.HEAD.getIndex());
                boolean autoFeeder = false;
                if(!helmet.isEmpty() && helmet.hasTag() && isItemHelmet(helmet.getItem())){
                    CompoundNBT nbt = helmet.getTag();
                    if(nbt.contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                        autoFeeder = true;
                    }
                }
                if(autoFeeder && event.player.canEat(false) && canWork(helmet)){
                    inv.mainInventory.stream()
                                     .filter(FeederHelmet::isStackEatable)
                                     .filter(stack -> canPlayerEat(event.player, stack))
                                     .forEach(stack -> {
                                         if(event.player.canEat(false)){
                                             AtomicBoolean hasEnergy = new AtomicBoolean(false);
                                             AtomicBoolean canEat = new AtomicBoolean(false);
                                             helmet.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
                                                 hasEnergy.set(true);
                                                 int energyConsumption = FeederConfig.GENERAL.ENERGY_CONSUMPTION.get();
                                                 if(energy.extractEnergy(energyConsumption, true) == energyConsumption){
                                                     energy.extractEnergy(energyConsumption, false);
                                                     canEat.set(true);
                                                 }
                                             });
                                             if(!hasEnergy.get() && helmet.isDamageable()){
                                                 helmet.setDamage(helmet.getDamage() + FeederConfig.GENERAL.DURABILITY.get());
                                                 if(helmet.getMaxDamage() - helmet.getDamage() <= 0){
                                                     helmet.setCount(0);
                                                 }
                                                 canEat.set(true);
                                             }
                                             if(canEat.get()){
                                                 ForgeEventFactory.onItemUseStart(event.player, stack, 0);
                                                 ItemStack result = stack.getItem().onItemUseFinish(stack, event.player.world, event.player);
                                                 ForgeEventFactory.onItemUseFinish(event.player, stack, 0, result);
                                             }
                                         }
                                     });
                }
            }
        }
    }
    
    private static boolean isItemHelmet(Item item){
        return
            (
                item instanceof ArmorItem
                    && ((ArmorItem) item).getEquipmentSlot() == EquipmentSlotType.HEAD
                    && !FeederConfig.GENERAL.HELMET_BLACKLIST.get().contains(item.getRegistryName().toString())
            )
                || FeederConfig.GENERAL.HELMET_WHITELIST.get().contains(item.getRegistryName().toString());
    }
    
    private static boolean isStackEatable(@Nonnull ItemStack stack){
        return !stack.isEmpty()
            && !FeederConfig.GENERAL.FOOD_BLACKLIST.get().contains(stack.getItem().getRegistryName().toString())
            && (stack.getItem().isFood() || FeederConfig.GENERAL.FOOD_WHITELIST.get().contains(stack.getItem().getRegistryName().toString()));
    }
    
    private static boolean canPlayerEat(PlayerEntity player, ItemStack stack){
        if(!stack.isEmpty() && stack.getItem().isFood()){
            if(FeederConfig.GENERAL.WAIT_UNITL_FILL_ALL_HUNGER.get()){
                return player.getFoodStats().getFoodLevel() + stack.getItem().getFood().getHealing() <= 20;
            }
            return true;
        }
        return true;
    }
    
    private static boolean canWork(@Nonnull ItemStack stack){
        AtomicBoolean canWork = new AtomicBoolean(false);
        
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
            int energyConsumption = FeederConfig.GENERAL.ENERGY_CONSUMPTION.get();
            canWork.set(energy.extractEnergy(energyConsumption, true) == energyConsumption);
        });
        
        if(!canWork.get() && stack.isDamageable()){
            int newDmg = stack.getDamage() + FeederConfig.GENERAL.DURABILITY.get();
            canWork.set(newDmg < stack.getMaxDamage() || FeederConfig.GENERAL.CAN_BREAK.get());
        }
        
        return canWork.get();
    }
    
    @SubscribeEvent
    public static void anvilRepair(AnvilRepairEvent event){
        ItemStack toRepair = event.getItemInput();
        if(toRepair.hasTag() && !toRepair.getCapability(CapabilityEnergy.ENERGY).isPresent()){
            if(toRepair.getTag().contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                ItemStack result = event.getItemResult();
                if(result.hasTag()){
                    result.getTag().putBoolean("AutoFeederHelmet", toRepair.getTag().getBoolean("AutoFeederHelmet"));
                }else{
                    CompoundNBT nbt = new CompoundNBT();
                    nbt.putBoolean("AutoFeederHelmet", toRepair.getTag().getBoolean("AutoFeederHelmet"));
                    result.setTag(nbt);
                }
            }
        }
    }
    
}