package com.refinedmods.refinedstorage.mekanism.content;

import java.util.function.Supplier;
import javax.annotation.Nullable;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import static java.util.Objects.requireNonNull;

public final class Menus {
    @Nullable
    private static Supplier<MenuType<AbstractContainerMenu>> chemicalStorage;

    private Menus() {
    }

    public static MenuType<AbstractContainerMenu> getChemicalStorage() {
        return requireNonNull(chemicalStorage).get();
    }

    public static void setChemicalStorage(final Supplier<MenuType<AbstractContainerMenu>> supplier) {
        chemicalStorage = supplier;
    }
}
