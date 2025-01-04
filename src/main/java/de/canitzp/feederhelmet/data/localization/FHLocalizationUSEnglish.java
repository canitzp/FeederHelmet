package de.canitzp.feederhelmet.data.localization;

import de.canitzp.feederhelmet.FeederHelmet;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.LanguageProvider;

import java.util.concurrent.CompletableFuture;

public class FHLocalizationUSEnglish extends LanguageProvider{

    public FHLocalizationUSEnglish(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        super(output, FeederHelmet.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        this.add(FHLocalizationKeys.TAB, "Auto Feeder Helmet");

        this.add(FeederHelmet.FEEDER_HELMET_MODULE_ITEM.get(), "Feeder Helmet Module");
        this.add(FHLocalizationKeys.MODULE_FEEDING_DESCRIPTION, "Right click in World, while wearing helmet, to apply this module to the helmet.");
        this.add(FHLocalizationKeys.MODULE_FEEDING_INSTALLED, "Auto feeding mode");
        this.add(FHLocalizationKeys.MODULE_FEEDING_APPLYING_DONE, "Auto Feeder Module installed");
        this.add(FHLocalizationKeys.MODULE_FEEDING_REMOVING_DONE, "Auto Feeder Module removed");

        this.add(FeederHelmet.PHOTOSYNTHESIS_MODULE_ITEM.get(), "Photosynthesis Helmet Module");
        this.add(FHLocalizationKeys.MODULE_PHOTOSYNTHESIS_DESCRIPTION, "Can't be crafted and has no function yet!");
        this.add(FHLocalizationKeys.MODULE_PHOTOSYNTHESIS_INSTALLED, "Photosynthesis Module");
    }
}
