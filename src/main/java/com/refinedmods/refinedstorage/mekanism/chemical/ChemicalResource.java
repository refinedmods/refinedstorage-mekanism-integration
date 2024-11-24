package com.refinedmods.refinedstorage.mekanism.chemical;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceTag;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceType;
import com.refinedmods.refinedstorage.common.support.resource.ResourceTypes;

import java.util.List;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;

public record ChemicalResource(Chemical chemical) implements PlatformResourceKey {
    public static ChemicalResource ofChemicalStack(final ChemicalStack stack) {
        return new ChemicalResource(stack.getChemical());
    }

    @Override
    public long getInterfaceExportLimit() {
        return ResourceTypes.FLUID.getInterfaceExportLimit();
    }

    @Override
    public long getProcessingPatternLimit() {
        return Platform.INSTANCE.getBucketAmount() * 16;
    }

    @Override
    public List<ResourceTag> getTags() {
        return MekanismAPI.CHEMICAL_REGISTRY.wrapAsHolder(chemical)
            .tags()
            .flatMap(tagKey -> MekanismAPI.CHEMICAL_REGISTRY.getTag(tagKey).stream())
            .map(tag -> new ResourceTag(
                tag.key(),
                tag.stream().map(holder -> (PlatformResourceKey) new ChemicalResource(holder.value())).toList()
            )).toList();
    }

    @Override
    public ResourceType getResourceType() {
        return ChemicalResourceType.INSTANCE;
    }
}
