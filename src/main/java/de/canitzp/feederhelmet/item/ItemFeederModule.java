package de.canitzp.feederhelmet.item;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.*;
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
    public void appendHoverText(ItemStack stack, @Nullable World world, List<ITextComponent> tooltip, ITooltipFlag flag) {
        super.appendHoverText(stack, world, tooltip, flag);
        tooltip.add(new TranslationTextComponent("item.feederhelmet:feeder_helmet_module.desc").setStyle(Style.EMPTY.withColor(Color.fromLegacyFormat(TextFormatting.GRAY))));
    }
}
