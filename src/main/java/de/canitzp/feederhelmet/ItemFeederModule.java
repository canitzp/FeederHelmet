package de.canitzp.feederhelmet;

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
        super(new Properties().group(FeederHelmet.TAB).maxStackSize(1));
        //this.setRegistryName(FeederHelmet.MODID, "feeder_helmet_module");
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(new TranslationTextComponent("item.feederhelmet:feeder_helmet_module.desc").func_230530_a_(Style.field_240709_b_.func_240721_b_(TextFormatting.GRAY)));
    }
}
