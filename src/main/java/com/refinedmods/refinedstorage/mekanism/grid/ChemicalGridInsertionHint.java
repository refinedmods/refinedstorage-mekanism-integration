package com.refinedmods.refinedstorage.mekanism.grid;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.common.api.grid.GridInsertionHint;
import com.refinedmods.refinedstorage.common.support.tooltip.MouseClientTooltipComponent;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalUtil;

import java.util.Optional;

import mekanism.api.Action;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.mekanism.ChemicalResource.ofChemicalStack;

public class ChemicalGridInsertionHint implements GridInsertionHint {
    @Override
    public Optional<ClientTooltipComponent> getHint(final ItemStack stack) {
        return Optional.ofNullable(stack.getCapability(ChemicalUtil.ITEM_CAPABILITY))
            .map(handler -> handler.extractChemical(Long.MAX_VALUE, Action.SIMULATE))
            .filter(result -> !result.isEmpty())
            .map(this::createComponent);
    }

    private ClientTooltipComponent createComponent(final ChemicalStack result) {
        final ChemicalResource chemicalResource = ofChemicalStack(result);
        final String amount = result.getAmount() == Platform.INSTANCE.getBucketAmount() ? null : doFormat(result);
        return MouseClientTooltipComponent.resource(MouseClientTooltipComponent.Type.RIGHT, chemicalResource, amount);
    }

    private static String doFormat(final ChemicalStack result) {
        return RefinedStorageClientApi.INSTANCE.getResourceRendering(ChemicalResource.class)
            .formatAmount(result.getAmount());
    }
}
