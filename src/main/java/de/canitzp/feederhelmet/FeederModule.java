package de.canitzp.feederhelmet;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class FeederModule implements IHelmetModule{
    
    @Override
    public Item getCorrespondingModuleItem(){
        return FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get();
    }
    
    @Override
    public String getTagName(){
        return "feeder_module"; // todo change with breaking release
    }
    
    @Override
    public boolean isModuleApplicableTo(ItemStack stack){
        return FeederHelmet.isItemHelmet(stack);
    }
    
    @Override
    public void renderTooltip(@Nonnull ItemStack stack, @Nullable Player entityPlayer, List<Component> list, TooltipFlag flags){
        list.add(new TranslatableComponent("item.feederhelmet:feeder_helmet_module_installed.text").withStyle(ChatFormatting.YELLOW, ChatFormatting.ITALIC));
    }
    
    @Override
    public void updatePlayer(Player player, ItemStack helmetStack){
        if(player.canEat(false) && FeederHelmet.canDamageBeReducedOrEnergyConsumed(helmetStack)){
            for(ItemStack inventoryStack : player.getInventory().items){
                if(!FeederModule.canHelmetEatStack(player.level, inventoryStack) || !FeederModule.canPlayerEat(player, inventoryStack) || !player.canEat(false)){
                    continue;
                }
                AtomicBoolean hasEnergy = new AtomicBoolean(false);
                AtomicBoolean canEat = new AtomicBoolean(false);
                helmetStack.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy -> {
                    hasEnergy.set(true);
                    int energyConsumption = FeederConfig.GENERAL.ENERGY_CONSUMPTION.get();
                    if(helmetStack.hasTag()){
                        int energy1 = helmetStack.getTag().getInt("Energy");
                        if(energy1 >= energyConsumption){
                            helmetStack.getTag().putInt("Energy", energy1 - energyConsumption);
                            canEat.set(true);
                        }
                    }
                });
                if(!hasEnergy.get()){
                    if(helmetStack.isDamageableItem()){
                        helmetStack.setDamageValue(helmetStack.getDamageValue() + FeederConfig.GENERAL.DURABILITY.get());
                        if(helmetStack.getMaxDamage() - helmetStack.getDamageValue() <= 0){
                            helmetStack.setCount(0);
                        }
                    }
                    // removed out of damage check if, because there are non-damageable helmets
                    canEat.set(true);
                }
                if(canEat.get()){
                    ForgeEventFactory.onItemUseStart(player, inventoryStack, 0);
                    ItemStack result = inventoryStack.getItem().finishUsingItem(inventoryStack, player.getCommandSenderWorld(), player);
                    ForgeEventFactory.onItemUseFinish(player, inventoryStack, 0, result);
                    break;
                }
            }
        }
    }
    
    private static boolean canHelmetEatStack(Level level, @Nonnull ItemStack stack){
        if(stack.isEmpty()){
            return false;
        }
        if(ItemStackUtil.isFoodBlacklisted(stack)){
            return false;
        }
        if(ItemStackUtil.isFoodWhitelisted(stack)){
            return true;
        }
        if(FeederConfig.GENERAL.BLACKLIST_SMELTABLES.get()){
            if(ItemStackUtil.isSmeltable(level, stack)){
                return false;
            }
        }
        return ItemStackUtil.isEatable(stack);
    }
    
    private static boolean canPlayerEat(Player player, ItemStack stack){
        if(!stack.isEmpty() && stack.getItem().isEdible()){
            if(FeederConfig.GENERAL.WAIT_UNITL_FILL_ALL_HUNGER.get()){
                return player.getFoodData().getFoodLevel() + stack.getItem().getFoodProperties().getNutrition() <= 20 || (FeederConfig.GENERAL.IGNORE_WAITING_WHEN_LOW_HEART.get() && player.getHealth() <= 10.0F);
            }
            return true;
        }
        return true;
    }
}
