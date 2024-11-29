package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.core.NullableType;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;

import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import javax.annotation.Nullable;

import com.google.common.collect.AbstractIterator;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.capabilities.BlockCapabilityCache;

import static com.refinedmods.refinedstorage.mekanism.ChemicalResource.ofChemicalStack;

public class ChemicalCapabilityCache {
    private final BlockCapabilityCache<IChemicalHandler, @NullableType Direction> cache;

    public ChemicalCapabilityCache(final ServerLevel level, final BlockPos pos, final Direction direction) {
        this.cache = BlockCapabilityCache.create(ChemicalUtil.BLOCK_CAPABILITY, level, pos, direction);
    }

    public Optional<IChemicalHandler> getCapability() {
        return Optional.ofNullable(cache.getCapability());
    }

    public Iterator<ResourceKey> iterator() {
        return getCapability().map(handler -> (Iterator<ResourceKey>) new AbstractIterator<ResourceKey>() {
            private int index;

            @Nullable
            @Override
            protected ResourceKey computeNext() {
                if (index > handler.getChemicalTanks()) {
                    return endOfData();
                }
                for (; index < handler.getChemicalTanks(); ++index) {
                    final ChemicalStack slot = handler.getChemicalInTank(index);
                    if (!slot.isEmpty()) {
                        index++;
                        return ofChemicalStack(slot);
                    }
                }
                return endOfData();
            }
        }).orElse(Collections.emptyListIterator());
    }
}
