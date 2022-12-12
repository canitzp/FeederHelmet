package de.canitzp.feederhelmet.item;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPhotosynthesisModule extends Item {
    
    public ItemPhotosynthesisModule(){
        super(new Properties().stacksTo(1));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(Component.translatable("item.feederhelmet:photosynthesis_helmet_module.desc").setStyle(Style.EMPTY.applyFormats(ChatFormatting.GRAY)));
    }
}
