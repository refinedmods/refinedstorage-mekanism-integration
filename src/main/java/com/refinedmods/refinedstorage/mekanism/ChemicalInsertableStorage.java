package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

import static com.refinedmods.refinedstorage.mekanism.ChemicalUtil.toMekanismAction;

public class ChemicalInsertableStorage implements InsertableStorage {
    private final ChemicalCapabilityCache capabilityCache;

    public ChemicalInsertableStorage(final ChemicalCapabilityCache capabilityCache) {
        this.capabilityCache = capabilityCache;
    }

    public long getAmount(final ResourceKey resource) {
        if (!(resource instanceof ChemicalResource chemicalResource)) {
            return 0;
        }
        return capabilityCache.getCapability()
            .map(fluidHandler -> ChemicalUtil.getCurrentAmount(fluidHandler, chemicalResource))
            .orElse(0L);
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ChemicalResource chemicalResource)) {
            return 0;
        }
        return capabilityCache.getCapability()
            .map(handler -> insert(chemicalResource, amount, action, handler))
            .orElse(0L);
    }

    private long insert(final ChemicalResource resource,
                        final long amount,
                        final Action action,
                        final IChemicalHandler chemicalHandler) {
        final ChemicalStack stack = new ChemicalStack(resource.chemical(), amount);
        final ChemicalStack remainder = chemicalHandler.insertChemical(stack, toMekanismAction(action));
        return amount - remainder.getAmount();
    }
}
