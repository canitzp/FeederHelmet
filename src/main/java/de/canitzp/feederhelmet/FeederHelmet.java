package de.canitzp.feederhelmet;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import de.canitzp.feederhelmet.data.localization.FHLocalizationKeys;
import de.canitzp.feederhelmet.item.ItemFeederModule;
import de.canitzp.feederhelmet.item.ItemPhotosynthesisModule;
import de.canitzp.feederhelmet.module.FeederModule;
import de.canitzp.feederhelmet.module.IHelmetModule;
import de.canitzp.feederhelmet.module.PhotosynthesisModule;
import net.minecraft.core.Holder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.event.entity.item.ItemEvent;
import net.neoforged.neoforge.event.entity.player.AnvilRepairEvent;
import net.neoforged.neoforge.event.entity.player.ItemTooltipEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.PlayerInteractEvent;
import net.neoforged.neoforge.event.level.LevelEvent;
import net.neoforged.neoforge.event.tick.PlayerTickEvent;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

/**
 * @author canitzp
 */
@EventBusSubscriber
@Mod(FeederHelmet.MODID)
public class FeederHelmet{
    
    public static final String MODID = "feederhelmet";
    
    public static final Logger LOGGER = LogManager.getLogger(FeederHelmet.MODID);

    public static final DeferredRegister<CreativeModeTab> TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);
    public static final Holder<CreativeModeTab> TAB = TABS.register("tab", FeederTab::create);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZER = DeferredRegister.create(Registries.RECIPE_SERIALIZER, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(BuiltInRegistries.ITEM, MODID);
    public static final Supplier<ItemFeederModule> FEEDER_HELMET_MODULE_ITEM = ITEMS.register("feeder_helmet_module", ItemFeederModule::new);
    public static final Supplier<ItemPhotosynthesisModule> PHOTOSYNTHESIS_MODULE_ITEM = ITEMS.register("photosynthesis_helmet_module", ItemPhotosynthesisModule::new);

    public static final DeferredRegister.DataComponents DATA_COMPONENT_TYPE = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, MODID);
    public static final Supplier<DataComponentType<List<String>>> DC_MODULES = DATA_COMPONENT_TYPE.registerComponentType("modules", listBuilder -> listBuilder.persistent(Codec.STRING.listOf()).networkSynchronized(ByteBufCodecs.STRING_UTF8.apply(ByteBufCodecs.list())).cacheEncoding());

    public static final List<IHelmetModule> MODULES = new ArrayList<>();
    
    public FeederHelmet(IEventBus modEventBus, ModContainer modContainer) {
        LOGGER.info("Feeder Helmet loading...");
        MODULES.add(new FeederModule());
        MODULES.add(new PhotosynthesisModule());

        modContainer.registerConfig(ModConfig.Type.COMMON, FeederConfig.spec);

        TABS.register(modEventBus);
        RECIPE_SERIALIZER.register(modEventBus);
        ITEMS.register(modEventBus);
        DATA_COMPONENT_TYPE.register(modEventBus);
        LOGGER.info("Feeder Helmet loaded.");
    }
    
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void renderTooltips(ItemTooltipEvent event){
        if(!event.getItemStack().isEmpty()){
            for(IHelmetModule module : MODULES){
                if(FeederHelmet.hasModule(event.getItemStack(), module.getTagName())){
                    module.renderTooltip(event.getItemStack(), event.getEntity(), event.getToolTip(), event.getFlags());
                }
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerRightClicks(PlayerInteractEvent.RightClickItem event){
        ItemStack heldStack = event.getItemStack();
        if(isItemHelmet(heldStack)){
            // remove all modules from helmet
            if(event.getEntity().isShiftKeyDown() && anyModules(heldStack)){
                event.setCanceled(true);
                heldStack.getOrDefault(DC_MODULES, new ArrayList<String>()).stream().map(s -> MODULES.stream().filter(iHelmetModule -> iHelmetModule.getTagName().equals(s)).findFirst()).forEach(iHelmetModule -> {
                    iHelmetModule.ifPresent(iHelmetModule1 -> {
                        removeModule(heldStack, iHelmetModule1.getTagName());
                        event.getEntity().displayClientMessage(Component.translatable(FHLocalizationKeys.MODULE_FEEDING_REMOVING_DONE), true);
                        if (!event.getEntity().addItem(iHelmetModule1.getCorrespondingModuleItem().getDefaultInstance())) {
                            event.getEntity().drop(iHelmetModule1.getCorrespondingModuleItem().getDefaultInstance(), false);
                        }
                    });
                });
            }
        } else {
            ItemStack helmetStack = event.getEntity().getInventory().getArmor(EquipmentSlot.HEAD.getIndex());

            // add module to wear helmet
            if(isItemHelmet(helmetStack)){
                MODULES.stream().filter(iHelmetModule -> heldStack.is(iHelmetModule.getCorrespondingModuleItem())).findFirst().ifPresent(iHelmetModule -> {
                    if(!hasModule(helmetStack, iHelmetModule.getTagName())){
                        event.getEntity().displayClientMessage(Component.translatable(FHLocalizationKeys.MODULE_FEEDING_APPLYING_DONE), true);
                        addModule(helmetStack, iHelmetModule.getTagName());
                        if(!event.getEntity().isCreative()){
                            heldStack.shrink(1);
                        }
                        event.setCancellationResult(InteractionResult.SUCCESS);
                    }
                });
            }
        }
    }
    
    @SubscribeEvent
    public static void updatePlayer(PlayerTickEvent.Post event){
        if(!event.getEntity().level().isClientSide() && event.getEntity().getCommandSenderWorld().getGameTime() % FeederConfig.GENERAL.WAIT_TICKS.get() == 0){
            ItemStack helmetStack = event.getEntity().getInventory().armor.get(EquipmentSlot.HEAD.getIndex());
            for(IHelmetModule module : MODULES){
                if(FeederHelmet.hasModule(helmetStack, module.getTagName())){
                    module.updatePlayer(event.getEntity(), helmetStack);
                }
            }
        }
    }

    @SubscribeEvent
    public static void playerJoin(PlayerEvent.PlayerLoggedInEvent event){
        Player player = event.getEntity();
        NonNullList<ItemStack> armorInventory = player.getInventory().armor;
        NonNullList<ItemStack> mainInventory = player.getInventory().items;
        NonNullList<ItemStack> offHandInventory = player.getInventory().offhand;

        NonNullList<ItemStack> mergedInventory = NonNullList.create();
        mergedInventory.addAll(armorInventory);
        mergedInventory.addAll(mainInventory);
        mergedInventory.addAll(offHandInventory);

        for(ItemStack stack : mergedInventory){
            if(stack.has(DataComponents.CUSTOM_DATA)){
                CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();
                List<String> foundModules = new ArrayList<>();
                // update from ancient versions
                if(tag.contains("AutoFeederHelmet", Tag.TAG_BYTE)){
                    foundModules.add("feeder_module");
                    tag.remove("AutoFeederHelmet");
                    stack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(tag)).build());
                }
                // update from pre 1.20.6 versions
                if(tag.contains("modules", Tag.TAG_LIST)){
                    tag.getList("modules", Tag.TAG_STRING).forEach(tag1 -> foundModules.add(tag1.getAsString()));
                    tag.remove("modules");
                    stack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(tag)).build());
                }
                if(!foundModules.isEmpty()){
                    foundModules.forEach(s -> FeederHelmet.addModule(stack, s));
                }
            }
        }
    }
    
    // copy modules nbt from old to ew item stack
    @SubscribeEvent
    public static void anvilRepair(AnvilRepairEvent event){
        ItemStack toRepair = event.getLeft();
        ItemStack result = event.getOutput();

        if(toRepair.has(DC_MODULES)){
            result.applyComponents(DataComponentPatch.builder().set(DC_MODULES.get(), toRepair.get(DC_MODULES)).build());
        }
    }

    public static boolean isItemHelmet(ItemStack stack){
        return (stack.getItem() instanceof ArmorItem && stack.getItem().components().get(DataComponents.EQUIPPABLE).slot() == EquipmentSlot.HEAD && !ItemStackUtil.isHelmetBlacklisted(stack)) || ItemStackUtil.isHelmetWhitelisted(stack);
    }
    
    public static boolean canDamageBeReducedOrEnergyConsumed(@Nonnull ItemStack stack){
        AtomicBoolean canWork = new AtomicBoolean(false);
        
        IEnergyStorage energyCapability = stack.getCapability(Capabilities.EnergyStorage.ITEM);
        if(energyCapability != null){
            canWork.set(true);
        }
        
        if(!canWork.get()){
            if(stack.isDamageableItem()){
                int newDmg = stack.getDamageValue() + FeederConfig.GENERAL.DURABILITY.get();
                if(FeederConfig.GENERAL.CAN_BREAK.get()){
                    canWork.set(newDmg <= stack.getMaxDamage());
                } else {
                    canWork.set(newDmg < stack.getMaxDamage());
                }
            } else {
                // There are super-op helmets that aren't damageable, so we need to account for that (eg: Wyvern Armor by Draconic Evolution)
                canWork.set(true);
            }
        }
        
        return canWork.get();
    }

    public static boolean anyModules(ItemStack stack){
        return stack.has(DC_MODULES) && !stack.get(DC_MODULES).isEmpty();
    }

    public static boolean hasModule(ItemStack stack, String module){
        return stack.has(DC_MODULES) && stack.get(DC_MODULES.get()).contains(module);
    }

    public static void addModule(ItemStack stack, String module){
        if(stack.has(DC_MODULES)){
            if (!stack.get(DC_MODULES).contains(module)) {
                List<String> modules = new ArrayList<>(stack.get(DC_MODULES));
                modules.add(module);
                stack.set(DC_MODULES, modules);
            }
        } else {
            stack.set(DC_MODULES, Lists.newArrayList(module));
        }
    }

    public static void removeModule(ItemStack stack, String module){
        if(stack.has(DC_MODULES)){
            List<String> modules = new ArrayList<>(stack.get(DC_MODULES)); // Immutable to mutable list
            modules.remove(module);
            stack.set(DC_MODULES, modules);
        }
    }

}
