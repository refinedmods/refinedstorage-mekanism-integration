package com.refinedmods.refinedstorage.mekanism.importer;

import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterSource;
import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterTransferStrategyImpl;
import com.refinedmods.refinedstorage.api.network.node.importer.ImporterTransferStrategy;
import com.refinedmods.refinedstorage.common.api.importer.ImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.common.api.upgrade.UpgradeState;
import com.refinedmods.refinedstorage.common.content.Items;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.neoforged.neoforge.fluids.FluidType;

public class ChemicalImporterTransferStrategyFactory implements ImporterTransferStrategyFactory {
    @Override
    public ImporterTransferStrategy create(final ServerLevel level,
                                           final BlockPos pos,
                                           final Direction direction,
                                           final UpgradeState upgradeState,
                                           final AmountOverride amountOverride) {
        final ImporterSource source = new ChemicalImporterSource(new ChemicalCapabilityCache(
            level,
            pos,
            direction
        ), amountOverride);
        final int transferQuota = upgradeState.has(Items.INSTANCE.getStackUpgrade())
            ? FluidType.BUCKET_VOLUME * 64
            : FluidType.BUCKET_VOLUME;
        return new ImporterTransferStrategyImpl(source, transferQuota);
    }
}
