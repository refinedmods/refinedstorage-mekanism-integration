package com.refinedmods.refinedstorage.mekanism.chemical;

import com.refinedmods.refinedstorage.api.grid.operations.GridExtractMode;
import com.refinedmods.refinedstorage.api.grid.operations.GridOperations;
import com.refinedmods.refinedstorage.common.api.grid.Grid;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridExtractionStrategy;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;

import javax.annotation.Nullable;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.mekanism.MekanismUtil.toMekanismAction;

public class ChemicalGridExtractionStrategy implements GridExtractionStrategy {
    private final AbstractContainerMenu menu;
    private final GridOperations gridOperations;

    public ChemicalGridExtractionStrategy(final AbstractContainerMenu containerMenu,
                                          final ServerPlayer player,
                                          final Grid grid) {
        this.menu = containerMenu;
        this.gridOperations = grid.createOperations(ChemicalResourceType.INSTANCE, player);
    }

    @Override
    public boolean onExtract(final PlatformResourceKey resource,
                             final GridExtractMode extractMode,
                             final boolean cursor) {
        if (resource instanceof ChemicalResource chemicalResource && isChemicalContainerOnCursor()) {
            extractWithContainerOnCursor(chemicalResource, extractMode);
            return true;
        }
        return false;
    }

    @Nullable
    private IChemicalHandler getChemicalStorage(final ItemStack stack) {
        return stack.getCapability(ChemicalResourceType.ITEM_CAPABILITY);
    }

    private void extractWithContainerOnCursor(final ChemicalResource chemicalResource,
                                              final GridExtractMode mode) {
        gridOperations.extract(chemicalResource, mode, (resource, amount, action, source) -> {
            if (!(resource instanceof ChemicalResource(Chemical chemical))) {
                return 0;
            }
            final IChemicalHandler destination = getChemicalStorage(menu.getCarried());
            if (destination == null) {
                return 0;
            }
            final ChemicalStack toInsert = new ChemicalStack(chemical, amount);
            final ChemicalStack remainder = destination.insertChemical(toInsert, toMekanismAction(action));
            return amount - remainder.getAmount();
        });
    }

    private boolean isChemicalContainerOnCursor() {
        return getChemicalStorage(menu.getCarried()) != null;
    }
}
