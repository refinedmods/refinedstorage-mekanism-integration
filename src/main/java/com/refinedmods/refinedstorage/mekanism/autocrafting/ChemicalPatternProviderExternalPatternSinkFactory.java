package com.refinedmods.refinedstorage.mekanism.autocrafting;

import com.refinedmods.refinedstorage.common.api.autocrafting.PatternProviderExternalPatternSinkFactory;
import com.refinedmods.refinedstorage.common.api.autocrafting.PlatformPatternProviderExternalPatternSink;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class ChemicalPatternProviderExternalPatternSinkFactory implements PatternProviderExternalPatternSinkFactory {
    @Override
    public PlatformPatternProviderExternalPatternSink create(final ServerLevel level,
                                                             final BlockPos pos,
                                                             final Direction direction) {
        final ChemicalCapabilityCache capabilityCache = new ChemicalCapabilityCache(level, pos, direction);
        return new ChemicalPatternProviderExternalPatternSink(capabilityCache);
    }
}
