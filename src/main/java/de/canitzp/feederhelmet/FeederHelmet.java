package de.canitzp.feederhelmet;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.RecipeManager;
import net.minecraft.item.crafting.ShapedRecipe;
import net.minecraft.item.crafting.ShapelessRecipe;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.Tag;
import net.minecraft.tags.TagCollection;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author canitzp
 */
@Mod(FeederHelmet.MODID)
public class FeederHelmet{
    
    public static final String MODID = "feederhelmet";
    
    public static final ItemFeederModule feederModule = new ItemFeederModule();
    
    public FeederHelmet(){
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FeederConfig.spec);
    }
    
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModEvents{
    
        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> reg){
            reg.getRegistry().register(feederModule);
        }
    
    }
    
    @Mod.EventBusSubscriber
    public static class ForgeEvents{
    
        @OnlyIn(Dist.CLIENT)
        @SubscribeEvent(priority = EventPriority.LOWEST)
        public static void renderTooltips(ItemTooltipEvent event){
            if(!event.getItemStack().isEmpty()){
                NBTTagCompound nbt = event.getItemStack().getTag();
                if(nbt != null && nbt.contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                    event.getToolTip().add(new TextComponentString(TextFormatting.YELLOW.toString() + TextFormatting.ITALIC.toString() + I18n.format("item.feederhelmet:feeder_helmet_module_installed.text") + TextFormatting.RESET.toString()));
                }
            }
        }
        
        @SubscribeEvent
        public static void onWorldLoad(WorldEvent.Load event){
            RecipeManager recipeManager = event.getWorld().getWorld().getRecipeManager();
            NonNullList<Ingredient> ingredients = NonNullList.create();
            ingredients.add(Ingredient.fromItems(feederModule));
            List<String> add_craft_items = FeederConfig.GENERAL.ADD_CRAFT_ITEMS.get();
            add_craft_items.stream()
                  .limit(7)
                  .map(s -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)))
                  .filter(Objects::nonNull)
                  .map(Ingredient::fromItems)
                  .forEach(ingredients::add);
            ForgeRegistries.ITEMS.getValues().stream()
                                 .filter(FeederHelmet::isItemHelmet)
                                 .forEach(helmet -> {
                                     NonNullList<Ingredient> ingredientsCopy = NonNullList.create();
                                     ingredientsCopy.addAll(ingredients);
                                     ingredientsCopy.add(Ingredient.fromItems(helmet));
                                     ItemStack helmetStack = new ItemStack(helmet);
                                     NBTTagCompound nbt = new NBTTagCompound();
                                     nbt.setBoolean("AutoFeederHelmet", true);
                                     helmetStack.setTag(nbt);
                                     ResourceLocation craftingId = new ResourceLocation(MODID, "feederhelmet_" + helmet.getRegistryName().getPath());
                                     ShapelessRecipe recipe = new ShapelessRecipe(craftingId, "", helmetStack, ingredientsCopy){
                                         @Nonnull
                                         @Override
                                         public ItemStack getCraftingResult(IInventory inv){
                                             NBTTagCompound nbt = new NBTTagCompound();
                                             for(int i = 0; i < inv.getSizeInventory(); i++){
                                                 ItemStack stack = inv.getStackInSlot(i);
                                                 if(!stack.isEmpty() && stack.getItem() instanceof ItemArmor){
                                                     if(stack.hasTag()){
                                                         nbt = stack.getTag();
                                                     }
                                                 }
                                             }
                                             ItemStack out = super.getCraftingResult(inv);
                                             nbt.setBoolean("AutoFeederHelmet", true);
                                             out.setTag(nbt);
                                             return out;
                                         }
    
                                         @Override
                                         public boolean matches(IInventory inv, World worldIn){
                                             if(super.matches(inv, worldIn)){
                                                 for(int i = 0; i < inv.getSizeInventory(); i++){
                                                     ItemStack stack = inv.getStackInSlot(i);
                                                     if(!stack.isEmpty() && stack.getItem() instanceof ItemArmor){
                                                         NBTTagCompound nbt = stack.getTag();
                                                         if(nbt != null && nbt.contains("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                                                             return false;
                                                         }
                                                     }
                                                 }
                                             }
                                             return super.matches(inv, worldIn);
                                         }
                                     };
                                     if(!recipeManager.getIds().contains(craftingId)){
                                         recipeManager.addRecipe(recipe);
                                     }
                                 });
        }
    
        @SubscribeEvent
        public static void updatePlayer(TickEvent.PlayerTickEvent event){
            if(event.phase == TickEvent.Phase.END && event.player.getEntityWorld().getGameTime() % FeederConfig.GENERAL.WAIT_TICKS.get() == 0){
                InventoryPlayer inv = event.player.inventory;
                ItemStack helmet = inv.armorInventory.get(EntityEquipmentSlot.HEAD.getIndex());
                boolean autoFeeder = false;
                if(!helmet.isEmpty() && helmet.hasTag() && isItemHelmet(helmet.getItem())){
                    NBTTagCompound nbt = helmet.getTag();
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
                                                 stack.getItem().onItemUseFinish(stack, event.player.world, event.player);
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
                item instanceof ItemArmor
                    && ((ItemArmor) item).getEquipmentSlot() == EntityEquipmentSlot.HEAD
                    && !FeederConfig.GENERAL.HELMET_BLACKLIST.get().contains(item.getRegistryName().toString())
            )
                || FeederConfig.GENERAL.HELMET_WHITELIST.get().contains(item.getRegistryName().toString());
    }
    
    private static boolean isStackEatable(@Nonnull ItemStack stack){
        return !stack.isEmpty()
            && !FeederConfig.GENERAL.FOOD_BLACKLIST.get().contains(stack.getItem().getRegistryName().toString())
            && (stack.getItem() instanceof ItemFood || FeederConfig.GENERAL.FOOD_WHITELIST.get().contains(stack.getItem().getRegistryName().toString()));
    }
    
    private static boolean canPlayerEat(EntityPlayer player, ItemStack stack){
        if(!stack.isEmpty() && stack.getItem() instanceof ItemFood){
            if(FeederConfig.GENERAL.WAIT_UNITL_FILL_ALL_HUNGER.get()){
                return player.getFoodStats().getFoodLevel() + ((ItemFood) stack.getItem()).getHealAmount(stack) <= 20;
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
                    result.getTag().setBoolean("AutoFeederHelmet", toRepair.getTag().getBoolean("AutoFeederHelmet"));
                }else{
                    NBTTagCompound nbt = new NBTTagCompound();
                    nbt.setBoolean("AutoFeederHelmet", toRepair.getTag().getBoolean("AutoFeederHelmet"));
                    result.setTag(nbt);
                }
            }
        }
    }
    
}