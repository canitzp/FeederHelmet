package de.canitzp.feederhelmet;

import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.Constants;

import javax.annotation.Nonnull;

public abstract class EnergyHandler {

    public static EnergyHandler get(@Nonnull ItemStack stack){
        if(!stack.hasTagCompound()){
            return null;
        }

        if(stack.getTagCompound().hasKey("Energy", Constants.NBT.TAG_INT)){
            return new Simple(stack, "Energy");
        }
        if(stack.getTagCompound().hasKey("energy", Constants.NBT.TAG_INT)){
            return new Simple(stack, "energy");
        }
        if(stack.getTagCompound().hasKey("enderio.darksteel.upgrade.energyUpgrade", Constants.NBT.TAG_COMPOUND)){
            return new EnderIOEnergyUpgrade(stack);
        }
        if(stack.getTagCompound().hasKey("charge", Constants.NBT.TAG_DOUBLE)){
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
            this.energy = stack.getTagCompound().getInteger(this.tagName);
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            this.energyAfterUsage = this.energy - energyToExtract;
            return this.energyAfterUsage >= 0;
        }

        @Override
        public void use() {
            super.stack.getTagCompound().setInteger(this.tagName, this.energyAfterUsage);
        }
    }

    public static class EnderIOEnergyUpgrade extends EnergyHandler {

        private int energy, energyAfterUsage;

        public EnderIOEnergyUpgrade(ItemStack stack) {
            super(stack);
            this.energy = stack.getTagCompound().getCompoundTag("enderio.darksteel.upgrade.energyUpgrade").getInteger("energy");
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            this.energyAfterUsage = this.energy - energyToExtract;
            return this.energyAfterUsage >= 0;
        }

        @Override
        public void use() {
            super.stack.getTagCompound().getCompoundTag("enderio.darksteel.upgrade.energyUpgrade").setInteger("energy", this.energyAfterUsage);
        }
    }

    public static class IC2 extends EnergyHandler {

        private static final double EU_CONVERSION_MODIFIER = 4.0D; // 1EU = 4FE

        private double energy, energyAfterUsage;

        public IC2(ItemStack stack) {
            super(stack);
            this.energy = stack.getTagCompound().getDouble("charge");
        }

        @Override
        public boolean canBeUsed(int energyToExtract) {
            double realEnergyToExtract = energyToExtract / EU_CONVERSION_MODIFIER;
            this.energyAfterUsage = this.energy - realEnergyToExtract;
            return this.energyAfterUsage >= 0.0D;
        }

        @Override
        public void use() {
            super.stack.getTagCompound().setDouble("charge", this.energyAfterUsage);
        }
    }
}
