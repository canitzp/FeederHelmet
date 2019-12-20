package de.canitzp.feederhelmet;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.item.crafting.ShapelessRecipes;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.player.AnvilRepairEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.ShapedOreRecipe;
import org.apache.commons.lang3.ArrayUtils;

import javax.annotation.Nonnull;
import java.util.Arrays;
import java.util.Objects;

/**
 * @author canitzp
 */
@Mod.EventBusSubscriber
@Mod(modid = FeederHelmet.MODID, name = FeederHelmet.MODNAME, version = FeederHelmet.MODVERSION, acceptedMinecraftVersions = FeederHelmet.MC_VERSIONS)
public class FeederHelmet{
    
    public static final String MODID = "feederhelmet";
    public static final String MODNAME = "FeederHelmet";
    public static final String MODVERSION = "@Version@";
    public static final String MC_VERSIONS = "1.12,1.12.1,1.12.2";
    
    public static final CreativeTabs TAB = new CreativeTabs(MODID){
        @Override
        public ItemStack createIcon(){
            return new ItemStack(feederModule);
        }
    
        @Override
        public void displayAllRelevantItems(NonNullList<ItemStack> stacks){
            stacks.add(new ItemStack(feederModule));
            for(Item item : ForgeRegistries.ITEMS){
                if(isItemHelmet(item)){
                    ItemStack helmet = new ItemStack(item);
                    NBTTagCompound nbt = new NBTTagCompound();
                    nbt.setBoolean("AutoFeederHelmet", true);
                    helmet.setTagCompound(nbt);
                    stacks.add(helmet);
                }
            }
        }
    };
    
    public static final ItemFeederModule feederModule = new ItemFeederModule();
    
    @SubscribeEvent
    public static void registerItems(RegistryEvent.Register<Item> reg){
        reg.getRegistry().register(feederModule);
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent
    public static void registerModels(ModelRegistryEvent event){
        ModelLoader.setCustomModelResourceLocation(feederModule, 0, new ModelResourceLocation(feederModule.getRegistryName(), "inventory"));
    }
    
    @SideOnly(Side.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderTooltips(ItemTooltipEvent event){
        if(!event.getItemStack().isEmpty()){
            NBTTagCompound nbt = event.getItemStack().getTagCompound();
            if(nbt != null && nbt.hasKey("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                event.getToolTip().add(TextFormatting.YELLOW.toString() + TextFormatting.ITALIC.toString() + I18n.format("item.feederhelmet:feeder_helmet_module_installed.text") + TextFormatting.RESET.toString());
            }
        }
    }
    
    @SubscribeEvent
    public static void registerRecipes(RegistryEvent.Register<IRecipe> reg){
        if(FeederConfig.HARD_MODULE_RECIPE){
            reg.getRegistry().register(new ShapedOreRecipe(feederModule.getRegistryName(), feederModule, " r ", "sbs", "iii", 'r', Items.BLAZE_ROD, 's', "logWood", 'b', "chest", 'i', "blockIron").setRegistryName(feederModule.getRegistryName()));
        }else{
            reg.getRegistry().register(new ShapedOreRecipe(feederModule.getRegistryName(), feederModule, " s ", "sbs", "iii", 's', "stickWood", 'b', Items.BOWL, 'i', "ingotIron").setRegistryName(feederModule.getRegistryName()));
        }
        NonNullList<Ingredient> ingredients = NonNullList.create();
        ingredients.add(Ingredient.fromItem(feederModule));
        String[] add_craft_items = FeederConfig.ADD_CRAFT_ITEMS;
        Arrays.stream(add_craft_items)
              .limit(7)
              .map(s -> ForgeRegistries.ITEMS.getValue(new ResourceLocation(s)))
              .filter(Objects::nonNull)
              .map(Ingredient::fromItem)
              .forEach(ingredients::add);
        ForgeRegistries.ITEMS.getValuesCollection().stream()
                             .filter(FeederHelmet::isItemHelmet)
                             .forEach(item -> {
                                 NonNullList<Ingredient> copy = NonNullList.create();
                                 copy.addAll(ingredients);
                                 copy.add(Ingredient.fromItem(item));
                                 ItemStack out = new ItemStack(item);
                                 NBTTagCompound nbt = new NBTTagCompound();
                                 nbt.setBoolean("AutoFeederHelmet", true);
                                 out.setTagCompound(nbt);
                                 reg.getRegistry().register(new ShapelessRecipes(MODID + ":feeder_" + item.getTranslationKey(), out, copy){
                                     @Nonnull
                                     @Override
                                     public ItemStack getCraftingResult(InventoryCrafting inv){
                                         NBTTagCompound nbt = new NBTTagCompound();
                                         for(int i = 0; i < inv.getSizeInventory(); i++){
                                             ItemStack stack = inv.getStackInSlot(i);
                                             if(!stack.isEmpty() && stack.getItem() instanceof ItemArmor){
                                                 if(stack.hasTagCompound()){
                                                     nbt = stack.getTagCompound();
                                                 }
                                             }
                                         }
                                         ItemStack out = super.getCraftingResult(inv);
                                         nbt.setBoolean("AutoFeederHelmet", true);
                                         out.setTagCompound(nbt);
                                         return out;
                                     }
    
                                     @Override
                                     public boolean matches(InventoryCrafting inv, World worldIn){
                                         if(super.matches(inv, worldIn)){
                                             for(int i = 0; i < inv.getSizeInventory(); i++){
                                                 ItemStack stack = inv.getStackInSlot(i);
                                                 if(!stack.isEmpty() && stack.getItem() instanceof ItemArmor){
                                                     NBTTagCompound nbt = stack.getTagCompound();
                                                     if(nbt != null && nbt.hasKey("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                                                         return false;
                                                     }
                                                 }
                                             }
                                         }
                                         return super.matches(inv, worldIn);
                                     }
                                 }.setRegistryName(MODID, "feeder_" + item.getTranslationKey()));
                             });
    }
    
    @SubscribeEvent
    public static void updatePlayer(TickEvent.PlayerTickEvent event){
        if(event.phase == TickEvent.Phase.END && event.player.getEntityWorld().getTotalWorldTime() % FeederConfig.WAIT_TICKS == 0){
            InventoryPlayer inv = event.player.inventory;
            ItemStack helmet = inv.armorInventory.get(EntityEquipmentSlot.HEAD.getIndex());
            boolean autoFeeder = false;
            if(!helmet.isEmpty() && helmet.hasTagCompound() && isItemHelmet(helmet.getItem())){
                NBTTagCompound nbt = helmet.getTagCompound();
                if(nbt.hasKey("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                    autoFeeder = true;
                }
            }
            if(autoFeeder && event.player.canEat(false) && canWork(helmet)){
                inv.mainInventory.stream()
                                 .filter(FeederHelmet::isStackEatable)
                                 .forEach(stack -> {
                                     if(event.player.canEat(false)){
                                         boolean canEat = false;
                                         if(helmet.hasCapability(CapabilityEnergy.ENERGY, null)){
                                             IEnergyStorage energy = helmet.getCapability(CapabilityEnergy.ENERGY, null);
                                             if(energy != null){
                                                 if(energy.extractEnergy(FeederConfig.ENERGY_CONSUPTION, true) == FeederConfig.ENERGY_CONSUPTION){
                                                     energy.extractEnergy(FeederConfig.ENERGY_CONSUPTION, false);
                                                     canEat = true;
                                                 }
                                             }
                                         }else if(helmet.isItemStackDamageable()){
                                             helmet.setItemDamage(helmet.getItemDamage() + FeederConfig.DURABILITY);
                                             if(helmet.getMaxDamage() - helmet.getItemDamage() <= 0){
                                                 helmet.setCount(0);
                                             }
                                             canEat = true;
                                         }
                                         if(canEat){
                                             ForgeEventFactory.onItemUseStart(event.player, stack, 0);
                                             ItemStack result = stack.getItem().onItemUseFinish(stack, event.player.world, event.player);
                                             ForgeEventFactory.onItemUseFinish(event.player, stack, 0, result);
                                         }
                                     }
                                 });
            }
        }
    }
    
    private static boolean isItemHelmet(Item item){
        return
            (
                item instanceof ItemArmor
                    && ((ItemArmor) item).armorType == EntityEquipmentSlot.HEAD
                    && !ArrayUtils.contains(FeederConfig.HELMET_BLACKLIST, item.getRegistryName().toString())
            )
                || ArrayUtils.contains(FeederConfig.HELMET_WHITELIST, item.getRegistryName().toString());
    }
    
    private static boolean isStackEatable(@Nonnull ItemStack stack){
        return
            !stack.isEmpty()
                && !ArrayUtils.contains(FeederConfig.FOOD_BLACKLIST, stack.getItem().getRegistryName().toString())
                &&
                (
                    stack.getItem() instanceof ItemFood
                        || ArrayUtils.contains(FeederConfig.FOOD_WHITELIST, stack.getItem().getRegistryName().toString())
                );
    }
    
    private static boolean canWork(@Nonnull ItemStack stack){
        if(stack.hasCapability(CapabilityEnergy.ENERGY, null)){
            IEnergyStorage energy = stack.getCapability(CapabilityEnergy.ENERGY, null);
            return energy != null && energy.extractEnergy(FeederConfig.ENERGY_CONSUPTION, true) == FeederConfig.ENERGY_CONSUPTION;
        }
        if(stack.isItemStackDamageable()){
            int newDmg = stack.getItemDamage() + FeederConfig.DURABILITY;
            return newDmg < stack.getMaxDamage() || FeederConfig.CAN_BREAK_HELMET;
        }
        return true;
    }
    
    @SubscribeEvent
    public static void anvilRepair(AnvilRepairEvent event){
        ItemStack toRepair = event.getItemInput();
        if(toRepair.hasTagCompound() && !toRepair.hasCapability(CapabilityEnergy.ENERGY, null)){
            if(toRepair.getTagCompound().hasKey("AutoFeederHelmet", Constants.NBT.TAG_BYTE)){
                ItemStack result = event.getItemResult();
                if(result.hasTagCompound()){
                    result.getTagCompound().setBoolean("AutoFeederHelmet", toRepair.getTagCompound().getBoolean("AutoFeederHelmet"));
                }else{
                    NBTTagCompound nbt = new NBTTagCompound();
                    nbt.setBoolean("AutoFeederHelmet", toRepair.getTagCompound().getBoolean("AutoFeederHelmet"));
                    result.setTagCompound(nbt);
                }
            }
        }
    }
    
}