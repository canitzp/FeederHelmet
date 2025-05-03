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
import net.minecraft.world.item.component.TooltipDisplay;

import java.util.List;
import java.util.function.Consumer;

public class ItemPhotosynthesisModule extends Item {
    
    public ItemPhotosynthesisModule(ResourceLocation resourceLocation){
        super(new Properties().setId(ResourceKey.create(Registries.ITEM, resourceLocation)).useItemDescriptionPrefix().stacksTo(1));
    }
    
    @Override
    public void appendHoverText(ItemStack stack, Item.TooltipContext context, TooltipDisplay tooltipDisplay, Consumer<Component> tooltip, TooltipFlag flag) {
        tooltip.accept(Component.translatable(FHLocalizationKeys.MODULE_PHOTOSYNTHESIS_DESCRIPTION).setStyle(Style.EMPTY.applyFormats(ChatFormatting.GRAY)));
    }
}
