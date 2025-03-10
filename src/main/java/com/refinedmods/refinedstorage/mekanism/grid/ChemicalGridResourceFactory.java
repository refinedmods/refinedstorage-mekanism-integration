package com.refinedmods.refinedstorage.mekanism.grid;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.repository.ResourceRepositoryMapper;
import com.refinedmods.refinedstorage.common.api.grid.GridResourceAttributeKeys;
import com.refinedmods.refinedstorage.common.api.grid.view.GridResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.Holder;
import net.neoforged.fml.ModList;

public class ChemicalGridResourceFactory implements ResourceRepositoryMapper<GridResource> {
    @Override
    public GridResource apply(final ResourceKey resource) {
        final ChemicalResource chemicalResource = (ChemicalResource) resource;
        final String name = getName(chemicalResource);
        final String modId = getModId(chemicalResource);
        final String modName = getModName(modId);
        final Set<String> tags = getTags(chemicalResource.chemical());
        final String tooltip = getTooltip(chemicalResource);
        return new ChemicalGridResource(chemicalResource, name, Map.of(
            GridResourceAttributeKeys.MOD_ID, Set.of(modId),
            GridResourceAttributeKeys.MOD_NAME, Set.of(modName),
            GridResourceAttributeKeys.TAGS, tags,
            GridResourceAttributeKeys.TOOLTIP, Set.of(tooltip)
        ));
    }

    private Set<String> getTags(final Chemical chemical) {
        return MekanismAPI.CHEMICAL_REGISTRY.getResourceKey(chemical)
            .flatMap(MekanismAPI.CHEMICAL_REGISTRY::getHolder)
            .stream()
            .flatMap(Holder::tags)
            .map(tagKey -> tagKey.location().getPath())
            .collect(Collectors.toSet());
    }

    private String getModId(final ChemicalResource chemical) {
        return MekanismAPI.CHEMICAL_REGISTRY.getKey(chemical.chemical()).getNamespace();
    }

    private String getTooltip(final ChemicalResource resource) {
        return getName(resource);
    }

    private String getModName(final String modId) {
        return ModList
            .get()
            .getModContainerById(modId)
            .map(container -> container.getModInfo().getDisplayName())
            .orElse("");
    }

    private String getName(final ChemicalResource chemical) {
        return chemical.chemical().getTextComponent().getString();
    }
}
