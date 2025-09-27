package com.refinedmods.refinedstorage.mekanism.grid;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.resource.repository.ResourceRepositoryMapper;
import com.refinedmods.refinedstorage.common.api.grid.GridResourceAttributeKeys;
import com.refinedmods.refinedstorage.common.api.grid.view.GridResource;
import com.refinedmods.refinedstorage.common.api.grid.view.GridResourceAttributeKey;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;

import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.google.common.base.Supplier;
import com.google.common.base.Suppliers;
import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import net.minecraft.core.Holder;
import net.neoforged.fml.ModList;

public class ChemicalGridResourceRepositoryMapper implements ResourceRepositoryMapper<GridResource> {
    @Override
    public GridResource apply(final ResourceKey resource) {
        final ChemicalResource chemicalResource = (ChemicalResource) resource;
        final String name = getName(chemicalResource);
        final String modId = getModId(chemicalResource);
        final String modName = getModName(modId);
        final Set<String> tags = getTags(chemicalResource.chemical());
        final String tooltip = getTooltip(chemicalResource);
        final Map<GridResourceAttributeKey, Supplier<Set<String>>> attributes = Map.of(
            GridResourceAttributeKeys.MOD_ID, Suppliers.ofInstance(Set.of(modId)),
            GridResourceAttributeKeys.MOD_NAME, Suppliers.ofInstance(Set.of(modName)),
            GridResourceAttributeKeys.TAGS, Suppliers.ofInstance(tags),
            GridResourceAttributeKeys.TOOLTIP, Suppliers.ofInstance(Set.of(tooltip))
        );
        return new ChemicalGridResource(chemicalResource, name,
            k -> attributes.getOrDefault(k, Collections::emptySet).get());
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
