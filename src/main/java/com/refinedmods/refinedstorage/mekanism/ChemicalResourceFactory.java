package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceFactory;

import java.util.Optional;

import mekanism.api.Action;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.mekanism.ChemicalResource.ofChemicalStack;

class ChemicalResourceFactory implements ResourceFactory {
    @Override
    public Optional<ResourceAmount> create(final ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(ChemicalUtil.ITEM_CAPABILITY))
            .map(handler -> handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE))
            .map(chemicalStack -> new ResourceAmount(ofChemicalStack(chemicalStack), chemicalStack.getAmount()));
    }

    @Override
    public boolean isValid(final ResourceKey resourceKey) {
        return resourceKey instanceof ChemicalResource;
    }
}
