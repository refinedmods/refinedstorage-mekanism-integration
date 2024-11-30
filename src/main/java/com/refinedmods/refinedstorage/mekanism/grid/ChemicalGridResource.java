package com.refinedmods.refinedstorage.mekanism.grid;

import com.refinedmods.refinedstorage.api.grid.operations.GridExtractMode;
import com.refinedmods.refinedstorage.api.grid.view.GridResourceAttributeKey;
import com.refinedmods.refinedstorage.api.grid.view.GridView;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.grid.GridScrollMode;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridExtractionStrategy;
import com.refinedmods.refinedstorage.common.api.grid.strategy.GridScrollingStrategy;
import com.refinedmods.refinedstorage.common.api.grid.view.AbstractPlatformGridResource;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceType;
import com.refinedmods.refinedstorage.common.support.tooltip.MouseClientTooltipComponent;
import com.refinedmods.refinedstorage.mekanism.ChemicalRenderer;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import com.refinedmods.refinedstorage.mekanism.ChemicalUtil;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import javax.annotation.Nullable;

import mekanism.api.Action;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.ItemStack;

public class ChemicalGridResource extends AbstractPlatformGridResource<ChemicalResource> {
    private final int id;
    private final ResourceRendering rendering;
    private final List<Component> tooltip;

    public ChemicalGridResource(final ChemicalResource resource,
                                final String name,
                                final Map<GridResourceAttributeKey, Set<String>> attributes,
                                final boolean autocraftable) {
        super(resource, name, attributes, autocraftable);
        this.id = MekanismAPI.CHEMICAL_REGISTRY.getId(resource.chemical());
        this.rendering = RefinedStorageApi.INSTANCE.getResourceRendering(ChemicalResource.class);
        this.tooltip = List.of(resource.chemical().getTextComponent());
    }

    @Override
    public int getRegistryId() {
        return id;
    }

    @Override
    public List<ClientTooltipComponent> getExtractionHints(final ItemStack carriedStack, final GridView view) {
        final ItemStack modifiedStack = carriedStack.copy();
        return Optional.ofNullable(modifiedStack.getCapability(ChemicalUtil.ITEM_CAPABILITY))
            .map(handler -> handler.insertChemical(
                new ChemicalStack(resource.chemical(), Platform.INSTANCE.getBucketAmount()),
                Action.EXECUTE
            ))
            .filter(remainder -> remainder.getAmount() != Platform.INSTANCE.getBucketAmount())
            .map(remainder -> MouseClientTooltipComponent.item(
                MouseClientTooltipComponent.Type.LEFT,
                modifiedStack,
                null
            )).stream().toList();
    }

    @Nullable
    @Override
    public ResourceAmount getAutocraftingRequest() {
        return new ResourceAmount(resource, Platform.INSTANCE.getBucketAmount());
    }

    @Override
    public boolean canExtract(final ItemStack carriedStack, final GridView view) {
        if (getAmount(view) == 0) {
            return false;
        }
        if (carriedStack.isEmpty()) {
            return true;
        }
        final ChemicalStack toFill = new ChemicalStack(resource.chemical(), view.getAmount(resource));
        return Optional.ofNullable(carriedStack.getCapability(ChemicalUtil.ITEM_CAPABILITY))
            .map(handler -> handler.insertChemical(toFill, Action.SIMULATE))
            .map(remainder -> remainder.getAmount() != toFill.getAmount())
            .orElse(false);
    }

    @Override
    public void onExtract(final GridExtractMode extractMode,
                          final boolean cursor,
                          final GridExtractionStrategy extractionStrategy) {
        extractionStrategy.onExtract(resource, extractMode, cursor);
    }

    @Override
    public void onScroll(final GridScrollMode scrollMode, final GridScrollingStrategy scrollingStrategy) {
        // no-op
    }

    @Override
    public void render(final GuiGraphics graphics, final int x, final int y) {
        ChemicalRenderer.render(graphics.pose(), x, y, resource.chemical());
    }

    @Override
    public String getDisplayedAmount(final GridView view) {
        return rendering.formatAmount(getAmount(view), true);
    }

    @Override
    public String getAmountInTooltip(final GridView view) {
        return rendering.formatAmount(getAmount(view));
    }

    @Override
    public boolean belongsToResourceType(final ResourceType resourceType) {
        return resourceType == ChemicalResourceType.INSTANCE;
    }

    @Override
    public List<Component> getTooltip() {
        return tooltip;
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage() {
        return Optional.empty();
    }
}
