package com.refinedmods.refinedstorage.mekanism.recipemod;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.RecipeModIngredientConverter;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;

import java.util.Optional;

import mekanism.api.chemical.Chemical;
import mekanism.api.chemical.ChemicalStack;
import net.neoforged.neoforge.fluids.FluidType;

import static com.refinedmods.refinedstorage.mekanism.ChemicalResource.ofChemicalStack;

public class JeiChemicalRecipeModIngredientConverter implements RecipeModIngredientConverter {
    @Override
    public Optional<PlatformResourceKey> convertToResource(final Object ingredient) {
        if (ingredient instanceof ChemicalStack stack) {
            return Optional.of(ofChemicalStack(stack));
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceAmount> convertToResourceAmount(final Object ingredient) {
        if (ingredient instanceof ChemicalStack stack) {
            return Optional.of(new ResourceAmount(ofChemicalStack(stack), stack.getAmount()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Object> convertToIngredient(final PlatformResourceKey resourceKey) {
        if (resourceKey instanceof ChemicalResource(Chemical chemical)) {
            return Optional.of(new ChemicalStack(chemical, FluidType.BUCKET_VOLUME));
        }
        return Optional.empty();
    }
}
