package de.canitzp.feederhelmet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

public class NBTHelper {

    public static boolean anyModulePresent(ItemStack stack){
        return !NBTHelper.getModulesList(stack).isEmpty();
    }
    
    public static boolean isModulePresent(String module, ItemStack stack){
        return stack.hasTag() && (stack.getTag().contains("modules", Tag.TAG_LIST)
            ? stack.getTag().getList("modules", Tag.TAG_STRING).stream()
                   .map(Tag::getAsString)
                   .anyMatch(module::equals)
            : stack.getTag().getBoolean(module)); // todo change with breaking release
    }
    
    private static ListTag getModulesList(ItemStack stack){
        CompoundTag nbt = stack.hasTag() ? stack.getTag() : new CompoundTag();
        return nbt.contains("modules", Tag.TAG_LIST) ? nbt.getList("modules", Tag.TAG_STRING) : new ListTag();
    }
    
    private static void setModulesList(ItemStack stack, ListTag listNBT){
        CompoundTag nbt = stack.hasTag() ? stack.getTag() : new CompoundTag();
        nbt.put("modules", listNBT);
        stack.setTag(nbt);
    }
    
    public static void addModule(String module, ItemStack stack){
        ListTag listNBT = NBTHelper.getModulesList(stack);
        if(listNBT.stream().map(Tag::getAsString).noneMatch(module::equals)){
            listNBT.add(StringTag.valueOf(module));
        }
        NBTHelper.setModulesList(stack, listNBT);
    }
    
    public static void removeModule(String module, ItemStack stack){
        ListTag newListNBT = new ListTag();
        ListTag currentListNBT = NBTHelper.getModulesList(stack);
        for(Tag inbt : currentListNBT){
            if(!module.equals(inbt.getAsString())){
                newListNBT.add(inbt);
            }
        }
        NBTHelper.setModulesList(stack, newListNBT);
    }
    
}
