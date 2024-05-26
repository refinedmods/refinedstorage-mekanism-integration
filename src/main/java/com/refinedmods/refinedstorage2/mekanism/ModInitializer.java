package com.refinedmods.refinedstorage2.mekanism;

import com.refinedmods.refinedstorage2.platform.common.util.IdentifierUtil;

import mekanism.api.MekanismAPI;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(ModInitializer.ID)
public final class ModInitializer {
    public static final String ID = "refinedstorage2_mekanism_integration";

    private static final Logger LOGGER = LoggerFactory.getLogger(ModInitializer.class);

    public ModInitializer(final IEventBus eventBus) {
        LOGGER.info(
            "Refined Storage 2 - Mekanism Integration has loaded. RS2 ModId: {}, Mekanism API version: {}",
            IdentifierUtil.MOD_ID,
            MekanismAPI.API_VERSION
        );
    }
}
