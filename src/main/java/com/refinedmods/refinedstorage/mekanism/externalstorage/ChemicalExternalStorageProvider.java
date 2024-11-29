package com.refinedmods.refinedstorage.mekanism.externalstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.ExtractableStorage;
import com.refinedmods.refinedstorage.api.storage.InsertableStorage;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.api.support.network.AmountOverride;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;
import com.refinedmods.refinedstorage.mekanism.ChemicalExtractableStorage;
import com.refinedmods.refinedstorage.mekanism.ChemicalInsertableStorage;

import java.util.Iterator;

class ChemicalExternalStorageProvider implements ExternalStorageProvider {
    private final ChemicalCapabilityCache capabilityCache;
    private final InsertableStorage insertTarget;
    private final ExtractableStorage extractTarget;

    ChemicalExternalStorageProvider(final ChemicalCapabilityCache capabilityCache) {
        this.capabilityCache = capabilityCache;
        this.insertTarget = new ChemicalInsertableStorage(capabilityCache, AmountOverride.NONE);
        this.extractTarget = new ChemicalExtractableStorage(capabilityCache, AmountOverride.NONE);
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        return capabilityCache.createAmountIterator();
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
