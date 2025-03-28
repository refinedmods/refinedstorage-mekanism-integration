package com.refinedmods.refinedstorage.mekanism.externalstorage;

import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.api.storage.externalstorage.ExternalStorageProviderFactory;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class ChemicalPlatformExternalStorageProviderFactory implements ExternalStorageProviderFactory {
    @Override
    public ExternalStorageProvider create(final ServerLevel level, final BlockPos pos, final Direction direction) {
        final ChemicalCapabilityCache capabilityCache = new ChemicalCapabilityCache(level, pos, direction);
        return new ChemicalExternalStorageProvider(capabilityCache);
    }
}
