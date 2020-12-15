package de.canitzp.feederhelmet;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.StringNBT;
import net.minecraftforge.common.util.Constants;

public class NBTHelper {

    public static boolean anyModulePresent(ItemStack stack){
        return !NBTHelper.getModulesList(stack).isEmpty();
    }
    
    public static boolean isModulePresent(String module, ItemStack stack){
        return stack.hasTag() && (stack.getTag().contains("modules", Constants.NBT.TAG_LIST)
            ? stack.getTag().getList("modules", Constants.NBT.TAG_STRING).stream()
                   .map(INBT::getString)
                   .anyMatch(module::equals)
            : stack.getTag().getBoolean(module)); // todo change with breaking release
    }
    
    private static ListNBT getModulesList(ItemStack stack){
        CompoundNBT nbt = stack.hasTag() ? stack.getTag() : new CompoundNBT();
        return nbt.contains("modules", Constants.NBT.TAG_LIST) ? nbt.getList("modules", Constants.NBT.TAG_STRING) : new ListNBT();
    }
    
    private static void setModulesList(ItemStack stack, ListNBT listNBT){
        CompoundNBT nbt = stack.hasTag() ? stack.getTag() : new CompoundNBT();
        nbt.put("modules", listNBT);
        stack.setTag(nbt);
    }
    
    public static void addModule(String module, ItemStack stack){
        ListNBT listNBT = NBTHelper.getModulesList(stack);
        if(listNBT.stream().map(INBT::getString).noneMatch(module::equals)){
            listNBT.add(StringNBT.valueOf(module));
        }
        NBTHelper.setModulesList(stack, listNBT);
    }
    
    public static void removeModule(String module, ItemStack stack){
        ListNBT newListNBT = new ListNBT();
        ListNBT currentListNBT = NBTHelper.getModulesList(stack);
        for(INBT inbt : currentListNBT){
            if(!module.equals(inbt.getString())){
                newListNBT.add(inbt);
            }
        }
        NBTHelper.setModulesList(stack, newListNBT);
    }
    
}
