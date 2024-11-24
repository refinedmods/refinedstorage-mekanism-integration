package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.mekanism.chemical.ChemicalStorageVariant;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.item.Item;

public final class Items {
    public static final Items INSTANCE = new Items();

    private final Map<ChemicalStorageVariant, Supplier<Item>> chemicalStorageParts =
        new EnumMap<>(ChemicalStorageVariant.class);
    private final Map<ChemicalStorageVariant, Supplier<Item>> chemicalStorageDisks =
        new EnumMap<>(ChemicalStorageVariant.class);

    private Items() {
    }

    public Item getChemicalStoragePart(final ChemicalStorageVariant variant) {
        return chemicalStorageParts.get(variant).get();
    }

    public void setChemicalStoragePart(final ChemicalStorageVariant variant, final Supplier<Item> supplier) {
        chemicalStorageParts.put(variant, supplier);
    }

    public Item getChemicalStorageDisk(final ChemicalStorageVariant variant) {
        return chemicalStorageDisks.get(variant).get();
    }

    public void setChemicalStorageDisk(final ChemicalStorageVariant variant, final Supplier<Item> supplier) {
        chemicalStorageDisks.put(variant, supplier);
    }
}
