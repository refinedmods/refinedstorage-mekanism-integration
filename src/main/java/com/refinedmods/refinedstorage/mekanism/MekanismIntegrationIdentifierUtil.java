package com.refinedmods.refinedstorage.mekanism;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public final class MekanismIntegrationIdentifierUtil {
    public static final String MOD_ID = "refinedstorage_mekanism_integration";

    private MekanismIntegrationIdentifierUtil() {
    }

    public static ResourceLocation createMekanismIntegrationIdentifier(final String value) {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, value);
    }

    public static String createMekanismIntegrationTranslationKey(final String category, final String value) {
        return String.format("%s.%s.%s", category, MOD_ID, value);
    }

    public static MutableComponent createMekanismIntegrationTranslation(final String category, final String value) {
        return Component.translatable(createMekanismIntegrationTranslationKey(category, value));
    }

    public static MutableComponent createMekanismIntegrationTranslation(final String category,
                                                                        final String value,
                                                                        final Object... args) {
        return Component.translatable(createMekanismIntegrationTranslationKey(category, value), args);
    }
}
