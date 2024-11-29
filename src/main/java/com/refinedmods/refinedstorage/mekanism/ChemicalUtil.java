package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.core.NullableType;

import mekanism.api.MekanismAPI;
import mekanism.api.chemical.ChemicalStack;
import mekanism.api.chemical.IChemicalHandler;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.ItemCapability;

public class ChemicalUtil {
    private static final ResourceLocation CHEMICAL_HANDLER_ID = ResourceLocation.fromNamespaceAndPath(
        MekanismAPI.MEKANISM_MODID,
        "chemical_handler"
    );
    public static final BlockCapability<IChemicalHandler, @NullableType Direction> BLOCK_CAPABILITY = BlockCapability
        .createSided(CHEMICAL_HANDLER_ID, IChemicalHandler.class);
    public static final ItemCapability<IChemicalHandler, Void> ITEM_CAPABILITY = ItemCapability
        .createVoid(CHEMICAL_HANDLER_ID, IChemicalHandler.class);

    private ChemicalUtil() {
    }

    public static long getCurrentAmount(final IChemicalHandler handler, final ChemicalResource resource) {
        long amount = 0;
        for (int i = 0; i < handler.getChemicalTanks(); ++i) {
            final ChemicalStack tank = handler.getChemicalInTank(i);
            if (tank.getChemical() == resource.chemical()) {
                amount += tank.getAmount();
            }
        }
        return amount;
    }

    public static mekanism.api.Action toMekanismAction(final Action action) {
        return switch (action) {
            case EXECUTE -> mekanism.api.Action.EXECUTE;
            case SIMULATE -> mekanism.api.Action.SIMULATE;
        };
    }
}
