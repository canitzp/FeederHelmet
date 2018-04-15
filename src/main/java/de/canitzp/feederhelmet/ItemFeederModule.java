package de.canitzp.feederhelmet;

import net.minecraft.client.resources.I18n;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

/**
 * @author canitzp
 */
public class ItemFeederModule extends Item {

    public ItemFeederModule(){
        this.setRegistryName(FeederHelmet.MODID, "feeder_helmet_module");
        this.setUnlocalizedName(this.getRegistryName().toString());
        this.setCreativeTab(CreativeTabs.TOOLS);
        this.setMaxStackSize(1);
    }

    @Override
    public void addInformation(ItemStack stack, @Nullable World worldIn, List<String> tooltip, ITooltipFlag flagIn) {
        super.addInformation(stack, worldIn, tooltip, flagIn);
        tooltip.add(I18n.format("item.feederhelmet:feeder_helmet_module.desc"));
    }
}
