package com.refinedmods.refinedstorage.mekanism.content;

import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageVariant;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.level.block.Block;

public final class Blocks {
    private static final Map<ChemicalStorageVariant, Supplier<Block>> CHEMICAL_STORAGE_BLOCKS = new EnumMap<>(
        ChemicalStorageVariant.class
    );

    private Blocks() {
    }

    public static Block getChemicalStorageBlock(final ChemicalStorageVariant variant) {
        return CHEMICAL_STORAGE_BLOCKS.get(variant).get();
    }

    public static void setChemicalStorageBlock(final ChemicalStorageVariant variant, final Supplier<Block> supplier) {
        CHEMICAL_STORAGE_BLOCKS.put(variant, supplier);
    }
}
