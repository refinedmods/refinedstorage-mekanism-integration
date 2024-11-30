package com.refinedmods.refinedstorage.mekanism.grid;

import com.refinedmods.refinedstorage.api.grid.operations.GridInsertMode;
import com.refinedmods.refinedstorage.api.grid.operations.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridInsertionStrategy;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import com.refinedmods.refinedstorage.mekanism.ChemicalUtil;

import javax.annotation.Nullable;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.mekanism.ChemicalResource.ofChemicalStack;
import static com.refinedmods.refinedstorage.mekanism.ChemicalUtil.toMekanismAction;

public class ChemicalGridInsertionStrategy implements GridInsertionStrategy {
    private final AbstractContainerMenu menu;
    private final GridOperations gridOperations;

    public ChemicalGridInsertionStrategy(final AbstractContainerMenu menu,
                                         final ServerPlayer player,
                                         final Grid grid) {
        this.menu = menu;
        this.gridOperations = grid.createOperations(ChemicalResourceType.INSTANCE, player);
    }

    @Override
    public boolean onInsert(final GridInsertMode insertMode, final boolean tryAlternatives) {
        final IChemicalHandler cursorStorage = getChemicalCursorStorage();
        if (cursorStorage == null) {
            return false;
        }
        if (cursorStorage.getChemicalTanks() == 0) {
            return false;
        }
        final ChemicalStack extractableResource = cursorStorage.getChemicalInTank(0);
        if (extractableResource.isEmpty()) {
            return false;
        }
        final ChemicalResource chemicalResource = ofChemicalStack(extractableResource);
        gridOperations.insert(chemicalResource, insertMode, (resource, amount, action, source) -> {
            if (!(resource instanceof ChemicalResource(Chemical chemical))) {
                return 0;
            }
            final ChemicalStack toDrain = new ChemicalStack(chemical, amount);
            final ChemicalStack drained = cursorStorage.extractChemical(toDrain, toMekanismAction(action));
            return drained.getAmount();
        });
        return true;
    }

    @Nullable
    private IChemicalHandler getChemicalCursorStorage() {
        return getChemicalStorage(menu.getCarried());
    }

    @Nullable
    private IChemicalHandler getChemicalStorage(final ItemStack stack) {
        return stack.getCapability(ChemicalUtil.ITEM_CAPABILITY);
    }

    @Override
    public boolean onTransfer(final int slotIndex) {
        throw new UnsupportedOperationException();
    }
}
