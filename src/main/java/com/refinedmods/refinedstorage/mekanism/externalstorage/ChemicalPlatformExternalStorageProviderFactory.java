package com.refinedmods.refinedstorage.mekanism.externalstorage;

import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.api.storage.externalstorage.PlatformExternalStorageProviderFactory;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class ChemicalPlatformExternalStorageProviderFactory implements PlatformExternalStorageProviderFactory {
    @Override
    public Optional<ExternalStorageProvider> create(final ServerLevel level,
                                                    final BlockPos pos,
                                                    final Direction direction) {
        final ChemicalCapabilityCache capabilityCache = new ChemicalCapabilityCache(level, pos, direction);
        return capabilityCache.getCapability().map(capability -> new ChemicalExternalStorageProvider(capabilityCache));
    }

    @Override
    public int getPriority() {
        return -2;
    }
}
