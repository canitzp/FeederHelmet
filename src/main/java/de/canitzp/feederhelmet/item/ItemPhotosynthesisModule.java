package de.canitzp.feederhelmet.item;

import de.canitzp.feederhelmet.data.localization.FHLocalizationKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

public class ItemPhotosynthesisModule extends Item {
    
    public ItemPhotosynthesisModule(){
        super(new Properties().stacksTo(1));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, context, tooltip, flagIn);
        tooltip.add(Component.translatable(FHLocalizationKeys.MODULE_PHOTOSYNTHESIS_DESCRIPTION).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GRAY)));
    }
}
