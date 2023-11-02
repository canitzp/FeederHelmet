package de.canitzp.feederhelmet.data.localization;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

public class FHLocalizationUSEnglish extends LanguageProvider{

    public FHLocalizationUSEnglish(PackOutput output) {
        super(output, FeederHelmet.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(FHLocalizationKeys.TAB, "Auto Feeder Helmet");

        this.add(FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get(), "Feeder Helmet Module");
        this.add(FHLocalizationKeys.MODULE_FEEDING_DESCRIPTION, "Craft this together with a helmet in a upgrade table to never eat manually again!");
        this.add(FHLocalizationKeys.MODULE_FEEDING_INSTALLED, "Auto feeding mode");

        this.add(FeederHelmet.PHOTOSYNTHESIS_MODULE_ITEM.get(), "Photosynthesis Helmet Module");
        this.add(FHLocalizationKeys.MODULE_PHOTOSYNTHESIS_DESCRIPTION, "Can't be crafted and has no function yet!");
        this.add(FHLocalizationKeys.MODULE_PHOTOSYNTHESIS_INSTALLED, "Photosynthesis Module");
    }
}
