package de.canitzp.feederhelmet;

import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nonnull;

public abstract class EnergyHandler {

    public static EnergyHandler get(@Nonnull ItemStack stack){
        if(!stack.has(DataComponents.CUSTOM_DATA)){
            return null;
        }

        CompoundTag tag = stack.get(DataComponents.CUSTOM_DATA).copyTag();

        if(tag.contains("Energy", Tag.TAG_INT)){
            return new Simple(stack, tag, "Energy");
        }
        if(tag.contains("energy", Tag.TAG_INT)){
            return new Simple(stack, tag, "energy");
        }
        if(tag.contains("enderio.darksteel.upgrade.energyUpgrade", Tag.TAG_COMPOUND)){
            return new EnderIOEnergyUpgrade(stack, tag);
        }
        if(tag.contains("mekData", Tag.TAG_COMPOUND)){
            CompoundTag mekData = tag.getCompound("mekData");
            if(mekData.contains("EnergyContainers", Tag.TAG_LIST)){
                return new MekanismMekaSuit(stack, tag, mekData.getList("EnergyContainers", Tag.TAG_COMPOUND));
            }
        }
        if(tag.contains("charge", Tag.TAG_DOUBLE)){
            return new IC2(stack, tag);
        }
        return null;
    }

    private ItemStack stack;
    private CompoundTag tag;

    public EnergyHandler(ItemStack stack, CompoundTag tag) {
        this.stack = stack;
        this.tag = tag;
    }

    public abstract boolean canBeUsed(int energyToExtract);

    public abstract void use();

    public static class Simple extends EnergyHandler {

        private String tagName;
        private int energy, energyAfterUsage;

        public Simple(ItemStack stack, CompoundTag tag, String tagName) {
            super(stack, tag);
            this.tagName = tagName;
            this.energy = tag.getInt(this.tagName);
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            this.energyAfterUsage = this.energy - energyToExtract;
            return this.energyAfterUsage >= 0;
        }

        @Override
        public void use() {
            super.tag.putInt(this.tagName, this.energyAfterUsage);
            super.stack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(super.tag)).build());
        }
    }

    public static class EnderIOEnergyUpgrade extends EnergyHandler {

        private int energy, energyAfterUsage;

        public EnderIOEnergyUpgrade(ItemStack stack, CompoundTag tag) {
            super(stack, tag);
            this.energy = tag.getCompound("enderio.darksteel.upgrade.energyUpgrade").getInt("energy");
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            this.energyAfterUsage = this.energy - energyToExtract;
            return this.energyAfterUsage >= 0;
        }

        @Override
        public void use() {
            super.tag.getCompound("enderio.darksteel.upgrade.energyUpgrade").putInt("energy", this.energyAfterUsage);
            super.stack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(super.tag)).build());
        }
    }

    public static class MekanismMekaSuit extends EnergyHandler {

        private ListTag tagEnergyContainers;
        private int energy, energyToExtract;

        public MekanismMekaSuit(ItemStack stack, CompoundTag tag, ListTag tagEnergyContainers) {
            super(stack, tag);
            this.tagEnergyContainers = tagEnergyContainers;
            for (Tag energyContainer : tagEnergyContainers) {
                if(energyContainer instanceof CompoundTag compound){
                    String storedAsString = compound.getString("stored");
                    if (NumberUtils.isParsable(storedAsString)) {
                        this.energy += NumberUtils.toInt(storedAsString, 0);
                    }
                }
            }
            this.energy = Math.round(this.energy / 2.5F); // mekanism stores in MJ and therefor it has to be converted by division with 2.5
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            this.energyToExtract = energyToExtract;
            return this.energy - energyToExtract >= 0;
        }

        @Override
        public void use() {
            this.energyToExtract = Math.round(this.energyToExtract * 2.5F); // energy conversion from RF/FE to MJ
            for (Tag energyContainer : this.tagEnergyContainers) {
                if(energyContainer instanceof CompoundTag compound){
                    String storedAsString = compound.getString("stored");
                    int stored = 0;
                    if(NumberUtils.isParsable(storedAsString)){
                        stored = NumberUtils.toInt(storedAsString, 0);
                    }
                    // calculate how much energy can be extracted
                    int energyAfterExtract = Math.max(0, stored - this.energyToExtract);
                    // calculate how much energy has to be extracted from the next storages
                    this.energyToExtract = stored - energyAfterExtract;
                    compound.putString("stored", Integer.toString(energyAfterExtract));
                    if(this.energyToExtract <= 0){
                        break;
                    }
                }
            }
            super.stack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(super.tag)).build());
        }
    }

    public static class IC2 extends EnergyHandler {

        private static final double EU_CONVERSION_MODIFIER = 4.0D; // 1EU = 4FE

        private double energy, energyAfterUsage;

        public IC2(ItemStack stack, CompoundTag tag) {
            super(stack, tag);
            this.energy = tag.getDouble("charge");
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            double realEnergyToExtract = energyToExtract / EU_CONVERSION_MODIFIER;
            this.energyAfterUsage = this.energy - realEnergyToExtract;
            return this.energyAfterUsage >= 0.0D;
        }

        @Override
        public void use() {
            super.tag.putDouble("charge", this.energyAfterUsage);
            super.stack.applyComponents(DataComponentPatch.builder().set(DataComponents.CUSTOM_DATA, CustomData.of(super.tag)).build());
        }
    }
}
