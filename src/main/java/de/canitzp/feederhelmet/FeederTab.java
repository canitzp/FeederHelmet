package de.canitzp.feederhelmet;

import de.canitzp.feederhelmet.data.localization.FHLocalizationKeys;
import de.canitzp.feederhelmet.module.IHelmetModule;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static de.canitzp.feederhelmet.FeederHelmet.FEEDER_HELMET_MODULE_ITEM;
import static de.canitzp.feederhelmet.FeederHelmet.MODULES;

public class FeederTab {

    public static CreativeModeTab create() {
        return CreativeModeTab.builder()
                .icon(() -> FEEDER_HELMET_MODULE_ITEM.get().getDefaultInstance())
                .title(Component.translatable(FHLocalizationKeys.TAB))
                .displayItems((parameters, output) -> {
                    for (IHelmetModule module : MODULES) {
                        output.accept(new ItemStack(module.getCorrespondingModuleItem()));
                        for (Item item : BuiltInRegistries.ITEM) {
                            if (module.isModuleApplicableTo(item.getDefaultInstance())) {
                                ItemStack stack = item.getDefaultInstance();
                                FeederHelmet.addModule(stack, module.getTagName());
                                output.accept(stack);
                            }
                        }
                    }
                }).build();
    }

}
