package com.refinedmods.refinedstorage.mekanism.exporter;

import com.refinedmods.refinedstorage.api.network.impl.node.exporter.ExporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.api.network.node.exporter.ExporterTransferStrategy;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.exporter.ExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.common.exporter.FuzzyExporterTransferStrategy;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;
import com.refinedmods.refinedstorage.mekanism.ChemicalInsertableStorage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;

public class ChemicalExporterTransferStrategyFactory implements ExporterTransferStrategyFactory {
    @Override
    public ExporterTransferStrategy create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction,
                                           final UpgradeState upgradeState,
                                           final AmountOverride amountOverride,
                                           final boolean fuzzyMode) {
        final ChemicalCapabilityCache capabilityCache = new ChemicalCapabilityCache(level, pos, direction);
        final InsertableStorage destination = new ChemicalInsertableStorage(
            capabilityCache,
            amountOverride
        );
        final long transferQuota = (upgradeState.has(Items.INSTANCE.getStackUpgrade()) ? 64 : 1)
            * Platform.INSTANCE.getBucketAmount();
        if (fuzzyMode) {
            return new FuzzyExporterTransferStrategy(destination, transferQuota);
        }
        return new ExporterTransferStrategyImpl(destination, transferQuota);
    }
}
