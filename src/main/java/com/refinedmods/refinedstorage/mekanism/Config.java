package com.refinedmods.refinedstorage.mekanism;

import net.neoforged.neoforge.common.ModConfigSpec;

import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationTranslationKey;

public final class Config {
    private final ModConfigSpec.Builder builder = new ModConfigSpec.Builder();
    private final ModConfigSpec spec;

    private final ChemicalStorageBlockEntry chemicalStorageBlock;

    public Config() {
        chemicalStorageBlock = new ChemicalStorageBlockEntry();
        spec = builder.build();
    }

    private static String translationKey(final String value) {
        return createMekanismIntegrationTranslationKey("config", "option." + value);
    }

    public ChemicalStorageBlockEntry getChemicalStorageBlock() {
        return chemicalStorageBlock;
    }

    public ModConfigSpec getSpec() {
        return spec;
    }

    public class ChemicalStorageBlockEntry {
        private final ModConfigSpec.LongValue sixtyFourBEnergyUsage;
        private final ModConfigSpec.LongValue twoHundredFiftySixBEnergyUsage;
        private final ModConfigSpec.LongValue thousandTwentyFourBEnergyUsage;
        private final ModConfigSpec.LongValue eightThousandHundredNinetyTwoBEnergyUsage;
        private final ModConfigSpec.LongValue creativeEnergyUsage;

        ChemicalStorageBlockEntry() {
            builder.translation(translationKey("chemicalStorageBlock")).push("chemicalStorageBlock");
            sixtyFourBEnergyUsage = builder
                .translation(translationKey("chemicalStorageBlock.sixtyFourBEnergyUsage"))
                .defineInRange(
                    "64bEnergyUsage",
                    2,
                    0,
                    Long.MAX_VALUE
                );
            twoHundredFiftySixBEnergyUsage = builder
                .translation(translationKey("chemicalStorageBlock.twoHundredFiftySixBEnergyUsage"))
                .defineInRange(
                    "256bEnergyUsage",
                    4,
                    0,
                    Long.MAX_VALUE
                );
            thousandTwentyFourBEnergyUsage = builder
                .translation(translationKey("chemicalStorageBlock.thousandTwentyFourBEnergyUsage"))
                .defineInRange(
                    "1024bEnergyUsage",
                    6,
                    0,
                    Long.MAX_VALUE
                );
            eightThousandHundredNinetyTwoBEnergyUsage = builder
                .translation(translationKey("chemicalStorageBlock.eightThousandHundredNinetyTwoBEnergyUsage"))
                .defineInRange(
                    "8192bEnergyUsage",
                    8,
                    0,
                    Long.MAX_VALUE
                );
            creativeEnergyUsage = builder
                .translation(translationKey("chemicalStorageBlock.creativeEnergyUsage"))
                .defineInRange(
                    "creativeEnergyUsage",
                    16,
                    0,
                    Long.MAX_VALUE
                );
            builder.pop();
        }

        public long get64bEnergyUsage() {
            return sixtyFourBEnergyUsage.get();
        }

        public long get256bEnergyUsage() {
            return twoHundredFiftySixBEnergyUsage.get();
        }

        public long get1024bEnergyUsage() {
            return thousandTwentyFourBEnergyUsage.get();
        }

        public long get8192bEnergyUsage() {
            return eightThousandHundredNinetyTwoBEnergyUsage.get();
        }

        public long getCreativeEnergyUsage() {
            return creativeEnergyUsage.get();
        }
    }
}
