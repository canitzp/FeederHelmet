package de.canitzp.feederhelmet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;

import static de.canitzp.feederhelmet.FeederHelmet.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD, modid = MODID)
public class FeederTab {

    @SubscribeEvent
    public static void registerCreativeTab(CreativeModeTabEvent.Register event){
        event.registerCreativeModeTab(new ResourceLocation(MODID, "tab"), builder -> {
            builder.icon(() -> FEEDER_HELMET_MODULE_ITEM.get().getDefaultInstance());
            builder.title(Component.translatable("tab.feederhelmet:general"));
            builder.displayItems((featureFlagSet, output, hasOp) -> {
                for(IHelmetModule module : MODULES){
                    output.accept(new ItemStack(module.getCorrespondingModuleItem()));
                    for(Item item : ForgeRegistries.ITEMS){
                        if(module.isModuleApplicableTo(item.getDefaultInstance())){
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
            });
        });
    }

}
