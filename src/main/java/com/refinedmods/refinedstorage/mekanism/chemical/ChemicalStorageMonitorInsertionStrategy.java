package com.refinedmods.refinedstorage.mekanism.chemical;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.network.Network;
import com.refinedmods.refinedstorage.api.network.storage.StorageNetworkComponent;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.api.storagemonitor.StorageMonitorInsertionStrategy;

import java.util.Optional;
import javax.annotation.Nullable;

import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.mekanism.chemical.ChemicalResource.ofChemicalStack;

public class ChemicalStorageMonitorInsertionStrategy implements StorageMonitorInsertionStrategy {
    @Override
    public Optional<ItemStack> insert(
        final ResourceKey configuredResource,
        final ItemStack stack,
        final Actor actor,
        final Network network
    ) {
        if (!(configuredResource instanceof ChemicalResource configuredChemicalResource)) {
            return Optional.empty();
        }
        final RootStorage rootStorage = network.getComponent(StorageNetworkComponent.class);
        final ItemStack modifiedStack = stack.copy();
        return Optional.ofNullable(modifiedStack.getCapability(ChemicalResourceType.ITEM_CAPABILITY))
            .map(handler -> handleInsert(actor, configuredChemicalResource, handler, rootStorage, modifiedStack));
    }

    @Nullable
    private ItemStack handleInsert(final Actor actor,
                                   final ChemicalResource configuredChemicalResource,
                                   final IChemicalHandler handler,
                                   final RootStorage rootStorage,
                                   final ItemStack modifiedStack) {
        final ChemicalStack extractedSimulated = handler.extractChemical(Long.MAX_VALUE, mekanism.api.Action.SIMULATE);
        if (extractedSimulated.isEmpty()) {
            return null;
        }
        final long insertedSimulated = tryInsert(actor, configuredChemicalResource, extractedSimulated, rootStorage);
        if (insertedSimulated == 0) {
            return null;
        }
        final ChemicalStack extracted = handler.extractChemical(insertedSimulated, mekanism.api.Action.EXECUTE);
        if (extracted.isEmpty()) {
            return null;
        }
        doInsert(actor, extracted, rootStorage);
        return modifiedStack;
    }

    private long tryInsert(final Actor actor,
                           final ChemicalResource configuredResource,
                           final ChemicalStack result,
                           final RootStorage rootStorage) {
        if (!result.getChemical().equals(configuredResource.chemical())) {
            return 0;
        }
        return rootStorage.insert(
            ofChemicalStack(result),
            result.getAmount(),
            Action.SIMULATE,
            actor
        );
    }

    private void doInsert(final Actor actor, final ChemicalStack result, final RootStorage rootStorage) {
        rootStorage.insert(
            ofChemicalStack(result),
            result.getAmount(),
            Action.EXECUTE,
            actor
        );
    }
}
