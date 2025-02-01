package com.refinedmods.refinedstorage.mekanism.autocrafting;

import com.refinedmods.refinedstorage.api.autocrafting.task.ExternalPatternSink;
import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.autocrafting.PlatformPatternProviderExternalPatternSink;
import com.refinedmods.refinedstorage.mekanism.ChemicalCapabilityCache;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;

import java.util.Collection;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.refinedmods.refinedstorage.mekanism.ChemicalUtil.toMekanismAction;

class ChemicalPatternProviderExternalPatternSink implements PlatformPatternProviderExternalPatternSink {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChemicalPatternProviderExternalPatternSink.class);

    private final ChemicalCapabilityCache capabilityCache;

    ChemicalPatternProviderExternalPatternSink(final ChemicalCapabilityCache capabilityCache) {
        this.capabilityCache = capabilityCache;
    }

    @Override
    public ExternalPatternSink.Result accept(final Collection<ResourceAmount> resources, final Action action) {
        return capabilityCache.getCapability()
            .map(handler -> accept(resources, action, handler))
            .orElse(ExternalPatternSink.Result.SKIPPED);
    }

    private ExternalPatternSink.Result accept(final Collection<ResourceAmount> resources,
                                              final Action action,
                                              final IChemicalHandler handler) {
        for (final ResourceAmount resource : resources) {
            if (resource.resource() instanceof ChemicalResource chemicalResource
                && !accept(action, handler, resource.amount(), chemicalResource)) {
                return ExternalPatternSink.Result.REJECTED;
            }
        }
        return ExternalPatternSink.Result.ACCEPTED;
    }

    private boolean accept(final Action action,
                           final IChemicalHandler handler,
                           final long amount,
                           final ChemicalResource chemicalResource) {
        final ChemicalStack stack = new ChemicalStack(chemicalResource.chemical(), amount);
        final ChemicalStack remainder = handler.insertChemical(stack, toMekanismAction(action));
        if (!remainder.isEmpty()) {
            if (action == Action.EXECUTE) {
                LOGGER.warn(
                    "{} unexpectedly didn't accept all of {}, the remainder has been voided",
                    handler,
                    stack
                );
            }
            return false;
        }
        return true;
    }

    @Override
    public boolean isEmpty() {
        return capabilityCache.getCapability().map(handler -> {
            for (int i = 0; i < handler.getChemicalTanks(); i++) {
                if (!handler.getChemicalInTank(i).isEmpty()) {
                    return false;
                }
            }
            return true;
        }).orElse(true);
    }
}
