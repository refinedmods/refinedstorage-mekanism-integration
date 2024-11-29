package com.refinedmods.refinedstorage.mekanism.importer;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.impl.node.importer.ImporterSource;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;
import com.refinedmods.refinedstorage.mekanism.ChemicalExtractableStorage;
import com.refinedmods.refinedstorage.mekanism.ChemicalInsertableStorage;

import java.util.Iterator;

class ChemicalImporterSource implements ImporterSource {
    private final ChemicalCapabilityCache chemicalCapabilityCache;
    private final ChemicalInsertableStorage insertTarget;
    private final ChemicalExtractableStorage extractTarget;

    ChemicalImporterSource(final ChemicalCapabilityCache chemicalCapabilityCache,
                           final AmountOverride amountOverride) {
        this.chemicalCapabilityCache = chemicalCapabilityCache;
        this.insertTarget = new ChemicalInsertableStorage(chemicalCapabilityCache, AmountOverride.NONE);
        this.extractTarget = new ChemicalExtractableStorage(chemicalCapabilityCache, amountOverride);
    }

    @Override
    public Iterator<ResourceKey> getResources() {
        return chemicalCapabilityCache.iterator();
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return extractTarget.extract(resource, amount, action, actor);
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        return insertTarget.insert(resource, amount, action, actor);
    }
}
