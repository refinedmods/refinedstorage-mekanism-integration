package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.core.Action;

public final class MekanismUtil {
    private MekanismUtil() {
    }

    public static mekanism.api.Action toMekanismAction(final Action action) {
        return switch (action) {
            case EXECUTE -> mekanism.api.Action.EXECUTE;
            case SIMULATE -> mekanism.api.Action.SIMULATE;
        };
    }
}
