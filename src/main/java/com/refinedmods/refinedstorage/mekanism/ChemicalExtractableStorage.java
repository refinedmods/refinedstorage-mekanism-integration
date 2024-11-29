package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;

import mekanism.api.chemical.ChemicalStack;

import static com.refinedmods.refinedstorage.mekanism.ChemicalUtil.toMekanismAction;

public class ChemicalExtractableStorage implements ExtractableStorage {
    private final ChemicalCapabilityCache capabilityCache;
    private final AmountOverride amountOverride;

    public ChemicalExtractableStorage(final ChemicalCapabilityCache capabilityCache,
                                      final AmountOverride amountOverride) {
        this.capabilityCache = capabilityCache;
        this.amountOverride = amountOverride;
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ChemicalResource chemicalResource)) {
            return 0;
        }
        return capabilityCache.getCapability().map(chemicalHandler -> {
            final long correctedAmount = amountOverride.overrideAmount(
                resource,
                amount,
                () -> ChemicalUtil.getCurrentAmount(chemicalHandler, chemicalResource)
            );
            if (correctedAmount == 0) {
                return 0L;
            }
            final ChemicalStack toExtractStack = new ChemicalStack(chemicalResource.chemical(), correctedAmount);
            final ChemicalStack drained = chemicalHandler.extractChemical(toExtractStack, toMekanismAction(action));
            return drained.getAmount();
        }).orElse(0L);
    }
}
