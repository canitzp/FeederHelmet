package de.canitzp.feederhelmet;

import de.canitzp.feederhelmet.data.localization.FHLocalizationKeys;
import de.canitzp.feederhelmet.module.IHelmetModule;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.ForgeRegistries;

import static de.canitzp.feederhelmet.FeederHelmet.*;

public class FeederTab {

    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .icon(() -> FEEDER_HELMET_MODULE_ITEM.get().getDefaultInstance())
                .title(Component.translatable(FHLocalizationKeys.TAB))
                .displayItems((parameters, output) -> {
                    for (IHelmetModule module : MODULES) {
                        output.accept(new ItemStack(module.getCorrespondingModuleItem()));
                        for (Item item : ForgeRegistries.ITEMS) {
                            if (module.isModuleApplicableTo(item.getDefaultInstance())) {
                                ItemStack stack = new ItemStack(item);
                                CompoundTag tag = new CompoundTag();
                                ListTag modulesList = new ListTag();
                                modulesList.add(StringTag.valueOf(module.getTagName()));
                                tag.put("modules", modulesList);
                                stack.setTag(tag);
                                output.accept(stack);
                            }
                        }
                    }
                }).build();
    }

}
