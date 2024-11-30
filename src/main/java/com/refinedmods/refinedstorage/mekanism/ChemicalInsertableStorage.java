package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

import static com.refinedmods.refinedstorage.mekanism.ChemicalUtil.toMekanismAction;

public class ChemicalInsertableStorage implements InsertableStorage {
    private final ChemicalCapabilityCache capabilityCache;
    private final AmountOverride amountOverride;

    public ChemicalInsertableStorage(final ChemicalCapabilityCache capabilityCache,
                                     final AmountOverride amountOverride) {
        this.capabilityCache = capabilityCache;
        this.amountOverride = amountOverride;
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ChemicalResource chemicalResource)) {
            return 0;
        }
        return capabilityCache.getCapability().map(chemicalHandler -> {
            final long correctedAmount = amountOverride.overrideAmount(
                chemicalResource,
                amount,
                () -> ChemicalUtil.getCurrentAmount(chemicalHandler, chemicalResource)
            );
            if (correctedAmount == 0) {
                return 0L;
            }
            return doInsert(chemicalResource, correctedAmount, action, chemicalHandler);
        }).orElse(0L);
    }

    private long doInsert(final ChemicalResource resource,
                          final long amount,
                          final Action action,
                          final IChemicalHandler chemicalHandler) {
        final ChemicalStack stack = new ChemicalStack(resource.chemical(), amount);
        final ChemicalStack remainder = chemicalHandler.insertChemical(stack, toMekanismAction(action));
        return amount - remainder.getAmount();
    }
}
