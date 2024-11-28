package com.refinedmods.refinedstorage.mekanism.chemical;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainerInsertStrategy;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.world.item.ItemStack;

public class ChemicalResourceContainerInsertStrategy implements ResourceContainerInsertStrategy {
    @Override
    public Optional<InsertResult> insert(final ItemStack container, final ResourceAmount resourceAmount) {
        if (!(resourceAmount.resource() instanceof ChemicalResource(Chemical chemical))) {
            return Optional.empty();
        }
        final ItemStack modifiedContainer = container.copy();
        return Optional.ofNullable(modifiedContainer.getCapability(ChemicalResourceType.ITEM_CAPABILITY))
            .map(handler -> handler.insertChemical(
                new ChemicalStack(chemical, resourceAmount.amount()),
                Action.EXECUTE
            ))
            .map(remainder -> resourceAmount.amount() - remainder.getAmount())
            .map(inserted -> new InsertResult(modifiedContainer, inserted));
    }

    // TODO: IFace max is wrong!
    @Override
    public Optional<ConversionInfo> getConversionInfo(final ResourceKey resource, final ItemStack carriedStack) {
        if (!(resource instanceof ChemicalResource(Chemical chemical))) {
            return Optional.empty();
        }
        final ItemStack modifiedStack = carriedStack.copy();
        return Optional.ofNullable(modifiedStack.getCapability(ChemicalResourceType.ITEM_CAPABILITY))
            .map(handler -> handler.insertChemical(
                new ChemicalStack(chemical, Platform.INSTANCE.getBucketAmount()),
                Action.EXECUTE
            ))
            .filter(ChemicalStack::isEmpty)
            .map(result -> new ConversionInfo(carriedStack, modifiedStack));
    }
}
