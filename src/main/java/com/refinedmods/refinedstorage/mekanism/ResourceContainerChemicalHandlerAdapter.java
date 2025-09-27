package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceContainer;

import mekanism.api.Action;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;

public class ResourceContainerChemicalHandlerAdapter implements IChemicalHandler {
    private final ResourceContainer container;

    public ResourceContainerChemicalHandlerAdapter(final ResourceContainer container) {
        this.container = container;
    }

    @Override
    public int getChemicalTanks() {
        return container.size();
    }

    @Override
    public ChemicalStack getChemicalInTank(final int tank) {
        final ResourceAmount resourceAmount = container.get(tank);
        if (resourceAmount == null || !(resourceAmount.resource() instanceof ChemicalResource(Chemical chemical))) {
            return ChemicalStack.EMPTY;
        }
        return new ChemicalStack(chemical, resourceAmount.amount());
    }

    @Override
    public void setChemicalInTank(final int tank, final ChemicalStack chemicalStack) {
        if (chemicalStack.isEmpty()) {
            container.remove(tank);
        } else {
            final long amount = Math.min(chemicalStack.getAmount(),
                ChemicalResourceType.INSTANCE.getInterfaceExportLimit());
            container.set(tank, new ResourceAmount(ChemicalResource.ofChemicalStack(chemicalStack), amount));
        }
    }

    @Override
    public long getChemicalTankCapacity(final int tank) {
        final ResourceKey resource = container.getResource(tank);
        if (resource == null || resource instanceof ChemicalResource) {
            return ChemicalResourceType.INSTANCE.getInterfaceExportLimit();
        }
        return 0;
    }

    @Override
    public boolean isValid(final int tank, final ChemicalStack chemicalStack) {
        return true;
    }

    @Override
    public ChemicalStack insertChemical(final int tank, final ChemicalStack chemicalStack, final Action action) {
        final ResourceAmount currentResource = container.get(tank);
        if (currentResource == null) {
            return insertChemicalInEmptyTank(tank, chemicalStack, action);
        } else if (currentResource.resource() instanceof ChemicalResource(Chemical otherChemical)
            && otherChemical == chemicalStack.getChemical()) {
            return insertChemicalInFilledTank(tank, chemicalStack, action, currentResource);
        }
        return chemicalStack;
    }

    private ChemicalStack insertChemicalInFilledTank(final int tank, final ChemicalStack chemicalStack,
                                                     final Action action, final ResourceAmount currentResource) {
        final long currentAmount = currentResource.amount();
        final long toInsert = Math.min(
            chemicalStack.getAmount(),
            ChemicalResourceType.INSTANCE.getInterfaceExportLimit() - currentAmount
        );
        if (toInsert <= 0) {
            return chemicalStack;
        }
        if (action == Action.EXECUTE) {
            container.set(tank, new ResourceAmount(currentResource.resource(), currentAmount + toInsert));
        }
        final long remainder = chemicalStack.getAmount() - toInsert;
        if (remainder <= 0) {
            return ChemicalStack.EMPTY;
        }
        return new ChemicalStack(chemicalStack.getChemical(), remainder);
    }

    private ChemicalStack insertChemicalInEmptyTank(final int tank, final ChemicalStack chemicalStack,
                                                    final Action action) {
        final long toInsert = Math.min(
            chemicalStack.getAmount(),
            ChemicalResourceType.INSTANCE.getInterfaceExportLimit()
        );
        if (action == Action.EXECUTE) {
            container.set(tank, new ResourceAmount(ChemicalResource.ofChemicalStack(chemicalStack), toInsert));
        }
        final long remainder = chemicalStack.getAmount() - toInsert;
        if (remainder <= 0) {
            return ChemicalStack.EMPTY;
        }
        return new ChemicalStack(chemicalStack.getChemical(), remainder);
    }

    @Override
    public ChemicalStack extractChemical(final int tank, final long amount, final Action action) {
        if (amount <= 0) {
            return ChemicalStack.EMPTY;
        }
        final ResourceAmount currentResource = container.get(tank);
        if (currentResource == null || !(currentResource.resource() instanceof ChemicalResource(Chemical chemical))) {
            return ChemicalStack.EMPTY;
        }
        final long toExtract = Math.min(amount, currentResource.amount());
        if (toExtract <= 0) {
            return ChemicalStack.EMPTY;
        }
        if (action == Action.EXECUTE) {
            container.shrink(tank, toExtract);
        }
        return new ChemicalStack(chemical, toExtract);
    }
}
