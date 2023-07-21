package de.canitzp.feederhelmet.module;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class PhotosynthesisModule implements IHelmetModule {

    @Override
    public Item getCorrespondingModuleItem() {
        return FeederHelmet.PHOTOSYNTHESIS_MODULE_ITEM.get();
    }

    @Override
    public String getTagName() {
        return "photosynthesis_module";
    }

    @Override
    public boolean isModuleApplicableTo(ItemStack stack) {
        return FeederHelmet.isItemHelmet(stack);
    }

    @Override
    public void renderTooltip(@NotNull ItemStack stack, @Nullable Player entityPlayer, List<Component> list, TooltipFlag flags) {
        list.add(Component.translatable("item.feederhelmet:photosynthesis_module_installed.text").withStyle(ChatFormatting.GREEN, ChatFormatting.ITALIC));
    }

    @Override
    public void updatePlayer(Player player, ItemStack helmet) {

    }
}
