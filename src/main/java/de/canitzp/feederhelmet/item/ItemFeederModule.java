package de.canitzp.feederhelmet.item;

import de.canitzp.feederhelmet.data.localization.FHLocalizationKeys;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

/**
 * @author canitzp
 */
public class ItemFeederModule extends Item {

    public ItemFeederModule(Identifier identifier){
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, identifier)).useItemDescriptionPrefix().stacksTo(1));
    }

    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(FHLocalizationKeys.MODULE_FEEDING_DESCRIPTION).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GRAY)));
    }
}
