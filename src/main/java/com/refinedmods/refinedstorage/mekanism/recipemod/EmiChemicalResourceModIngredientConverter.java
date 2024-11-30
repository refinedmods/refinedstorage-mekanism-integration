package com.refinedmods.refinedstorage.mekanism.recipemod;

import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.RecipeModIngredientConverter;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;

import java.util.Optional;

import dev.emi.emi.api.stack.EmiStack;
import mekanism.api.IMekanismAccess;
import mekanism.api.chemical.Chemical;
import net.neoforged.neoforge.fluids.FluidType;

import static com.refinedmods.refinedstorage.mekanism.ChemicalResource.ofChemicalStack;

public class EmiChemicalResourceModIngredientConverter implements RecipeModIngredientConverter {
    @Override
    public Optional<PlatformResourceKey> convertToResource(final Object ingredient) {
        if (ingredient instanceof EmiStack emiStack) {
            return IMekanismAccess.INSTANCE.emiHelper()
                .asChemicalStack(emiStack)
                .map(ChemicalResource::ofChemicalStack);
        }
        return Optional.empty();
    }

    @Override
    public Optional<ResourceAmount> convertToResourceAmount(final Object ingredient) {
        if (ingredient instanceof EmiStack emiStack) {
            return IMekanismAccess.INSTANCE.emiHelper()
                .asChemicalStack(emiStack)
                .map(chemical -> new ResourceAmount(ofChemicalStack(chemical), emiStack.getAmount()));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Object> convertToIngredient(final PlatformResourceKey resourceKey) {
        if (resourceKey instanceof ChemicalResource(Chemical chemical)) {
            return Optional.of(IMekanismAccess.INSTANCE.emiHelper().createEmiStack(chemical, FluidType.BUCKET_VOLUME));
        }
        return Optional.empty();
    }
}
