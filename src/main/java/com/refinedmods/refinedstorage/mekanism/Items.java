package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageVariant;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.item.Item;

public final class Items {
    private static final Map<ChemicalStorageVariant, Supplier<Item>> CHEMICAL_STORAGE_PARTS =
        new EnumMap<>(ChemicalStorageVariant.class);
    private static final Map<ChemicalStorageVariant, Supplier<Item>> CHEMICAL_STORAGE_DISKS =
        new EnumMap<>(ChemicalStorageVariant.class);

    private Items() {
    }

    public static Item getChemicalStoragePart(final ChemicalStorageVariant variant) {
        return CHEMICAL_STORAGE_PARTS.get(variant).get();
    }

    public static void setChemicalStoragePart(final ChemicalStorageVariant variant, final Supplier<Item> supplier) {
        CHEMICAL_STORAGE_PARTS.put(variant, supplier);
    }

    public static Item getChemicalStorageDisk(final ChemicalStorageVariant variant) {
        return CHEMICAL_STORAGE_DISKS.get(variant).get();
    }

    public static void setChemicalStorageDisk(final ChemicalStorageVariant variant, final Supplier<Item> supplier) {
        CHEMICAL_STORAGE_DISKS.put(variant, supplier);
    }
}
