package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;

import mekanism.api.chemical.ChemicalStack;

import static com.refinedmods.refinedstorage.mekanism.ChemicalUtil.toMekanismAction;

public class ChemicalExtractableStorage implements ExtractableStorage {
    private final ChemicalCapabilityCache capabilityCache;

    public ChemicalExtractableStorage(final ChemicalCapabilityCache capabilityCache) {
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
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ChemicalResource chemicalResource)) {
            return 0;
        }
        return capabilityCache.getCapability().map(chemicalHandler -> {
            final ChemicalStack toExtractStack = new ChemicalStack(chemicalResource.chemical(), amount);
            final ChemicalStack drained = chemicalHandler.extractChemical(toExtractStack, toMekanismAction(action));
            return drained.getAmount();
        }).orElse(0L);
    }
}
