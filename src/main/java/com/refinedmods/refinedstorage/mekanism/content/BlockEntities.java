package com.refinedmods.refinedstorage.mekanism.content;

import com.refinedmods.refinedstorage.common.api.support.network.AbstractNetworkNodeContainerBlockEntity;
import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageVariant;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

import net.minecraft.world.level.block.entity.BlockEntityType;

public final class BlockEntities {
    private static final Map<ChemicalStorageVariant,
        Supplier<BlockEntityType<AbstractNetworkNodeContainerBlockEntity<?>>>> CHEMICAL_STORAGE_BLOCKS =
        new EnumMap<>(ChemicalStorageVariant.class);

    private BlockEntities() {
    }

    public static BlockEntityType<AbstractNetworkNodeContainerBlockEntity<?>> getChemicalStorageBlock(
        final ChemicalStorageVariant variant
    ) {
        return CHEMICAL_STORAGE_BLOCKS.get(variant).get();
    }

    public static void setChemicalStorageBlock(
        final ChemicalStorageVariant variant,
        final Supplier<BlockEntityType<AbstractNetworkNodeContainerBlockEntity<?>>> supplier
    ) {
        CHEMICAL_STORAGE_BLOCKS.put(variant, supplier);
    }
}
