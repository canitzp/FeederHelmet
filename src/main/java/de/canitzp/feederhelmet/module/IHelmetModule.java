package de.canitzp.feederhelmet.module;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IHelmetModule {

    Item getCorrespondingModuleItem();
    
    // the tag name that is applied to the "modules" NBTList<String> for the item stack.
    String getTagName();
    
    // can this module be applied to the given helmet
    boolean isModuleApplicableTo(ItemStack stack);
    
    // additional information to render to the item stack tooltip. Only called when the module is applied to the item stack
    void renderTooltip(@Nonnull ItemStack stack, @Nullable Player entityPlayer, List<Component> list, TooltipFlag flags);
    
    void updatePlayer(Player player, ItemStack helmet);
    
}
