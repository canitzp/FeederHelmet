package de.canitzp.feederhelmet;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.lang3.math.NumberUtils;

import javax.annotation.Nonnull;

public abstract class EnergyHandler {

    public static EnergyHandler get(@Nonnull ItemStack stack){
        if(!stack.hasTag()){
            return null;
        }

        if(stack.getTag().contains("Energy", Tag.TAG_INT)){
            return new Simple(stack, "Energy");
        }
        if(stack.getTag().contains("energy", Tag.TAG_INT)){
            return new Simple(stack, "energy");
        }
        if(stack.getTag().contains("enderio.darksteel.upgrade.energyUpgrade", Tag.TAG_COMPOUND)){
            return new EnderIOEnergyUpgrade(stack);
        }
        if(stack.getTag().contains("mekData", Tag.TAG_COMPOUND)){
            CompoundTag mekData = stack.getTag().getCompound("mekData");
            if(mekData.contains("EnergyContainers", Tag.TAG_LIST)){
                return new MekanismMekaSuit(stack, mekData.getList("EnergyContainers", Tag.TAG_COMPOUND));
            }
        }
        if(stack.getTag().contains("charge", Tag.TAG_DOUBLE)){
            return new IC2(stack);
        }
        return null;
    }

    private ItemStack stack;

    public EnergyHandler(ItemStack stack) {
        this.stack = stack;
    }

    public abstract boolean canBeUsed(int energyToExtract);

    public abstract void use();

    public static class Simple extends EnergyHandler {

        private String tagName;
        private int energy, energyAfterUsage;

        public Simple(ItemStack stack, String tagName) {
            super(stack);
            this.tagName = tagName;
            this.energy = stack.getTag().getInt(this.tagName);
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            this.energyAfterUsage = this.energy - energyToExtract;
            return this.energyAfterUsage >= 0;
        }

        @Override
        public void use() {
            super.stack.getTag().putInt(this.tagName, this.energyAfterUsage);
        }
    }

    public static class EnderIOEnergyUpgrade extends EnergyHandler {

        private int energy, energyAfterUsage;

        public EnderIOEnergyUpgrade(ItemStack stack) {
            super(stack);
            this.energy = stack.getTag().getCompound("enderio.darksteel.upgrade.energyUpgrade").getInt("energy");
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            this.energyAfterUsage = this.energy - energyToExtract;
            return this.energyAfterUsage >= 0;
        }

        @Override
        public void use() {
            super.stack.getTag().getCompound("enderio.darksteel.upgrade.energyUpgrade").putInt("energy", this.energyAfterUsage);
        }
    }

    public static class MekanismMekaSuit extends EnergyHandler {

        private ListTag tagEnergyContainers;
        private int energy, energyToExtract;

        public MekanismMekaSuit(ItemStack stack, ListTag tagEnergyContainers) {
            super(stack);
            this.tagEnergyContainers = tagEnergyContainers;
            for (Tag tag : tagEnergyContainers) {
                if(tag instanceof CompoundTag compound){
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
                if(energyContainer instanceof CompoundTag tag){
                    String storedAsString = tag.getString("stored");
                    int stored = 0;
                    if(NumberUtils.isParsable(storedAsString)){
                        stored = NumberUtils.toInt(storedAsString, 0);
                    }
                    // calculate how much energy can be extracted
                    int energyAfterExtract = Math.max(0, stored - this.energyToExtract);
                    // calculate how much energy has to be extracted from the next storages
                    this.energyToExtract = stored - energyAfterExtract;
                    tag.putString("stored", Integer.toString(energyAfterExtract));
                    if(this.energyToExtract <= 0){
                        break;
                    }
                }
            }
        }
    }

    public static class IC2 extends EnergyHandler {

        private static final double EU_CONVERSION_MODIFIER = 4.0D; // 1EU = 4FE

        private double energy, energyAfterUsage;

        public IC2(ItemStack stack) {
            super(stack);
            this.energy = stack.getTag().getDouble("charge");
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            double realEnergyToExtract = energyToExtract / EU_CONVERSION_MODIFIER;
            this.energyAfterUsage = this.energy - realEnergyToExtract;
            return this.energyAfterUsage >= 0.0D;
        }

        @Override
        public void use() {
            super.stack.getTag().putDouble("charge", this.energyAfterUsage);
        }
    }
}
