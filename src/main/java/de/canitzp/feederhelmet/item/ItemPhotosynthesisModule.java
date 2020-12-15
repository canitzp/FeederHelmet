package de.canitzp.feederhelmet.item;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class ItemPhotosynthesisModule extends Item {
    
    public ItemPhotosynthesisModule(){
        super(new Properties().group(FeederHelmet.TAB).maxStackSize(1));
    }
    
    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("item.feederhelmet:photosynthesis_helmet_module.desc").setStyle(Style.EMPTY.setColor(Color.fromTextFormatting(TextFormatting.GRAY))));
    }
}
