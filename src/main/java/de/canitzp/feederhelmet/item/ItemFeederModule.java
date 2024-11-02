package de.canitzp.feederhelmet.item;

import de.canitzp.feederhelmet.data.localization.FHLocalizationKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;

import java.util.List;

/**
 * @author canitzp
 */
public class ItemFeederModule extends Item {

    public ItemFeederModule(ResourceLocation resourceLocation){
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, resourceLocation)).useItemDescriptionPrefix().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, List<Component> tooltip, TooltipFlag flagIn) {
        tooltip.add(Component.translatable(FHLocalizationKeys.MODULE_FEEDING_DESCRIPTION).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GRAY)));
    }
}
