package de.canitzp.feederhelmet.item;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author canitzp
 */
public class ItemFeederModule extends Item {

    public ItemFeederModule(){
        super(new Properties().tab(FeederHelmet.TAB).stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.appendHoverText(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("item.feederhelmet:feeder_helmet_module.desc").setStyle(Style.EMPTY.applyFormats(TextFormatting.GRAY)));
    }
}
