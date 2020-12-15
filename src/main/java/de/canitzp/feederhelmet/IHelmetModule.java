package de.canitzp.feederhelmet;

import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;

public interface IHelmetModule {

    Item getCorrespondingModuleItem();
    
    // the tag name that is applied to the "modules" NBTList<String> for the item stack.
    String getTagName();
    
    // can this module be applied to the given helmet
    boolean isModuleApplicableTo(Item item);
    
    // additional information to render to the item stack tooltip. Only called when the module is applied to the item stack
    void renderTooltip(@Nonnull ItemStack stack, @Nullable PlayerEntity entityPlayer, List<ITextComponent> list, ITooltipFlag flags);
    
    void updatePlayer(PlayerEntity player, ItemStack helmet);
    
}
