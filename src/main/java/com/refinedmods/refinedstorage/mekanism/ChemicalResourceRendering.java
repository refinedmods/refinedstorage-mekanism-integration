package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceRendering;
import com.refinedmods.refinedstorage.common.util.IdentifierUtil;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.api.chemical.Chemical;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.fluids.FluidType;

public class ChemicalResourceRendering implements ResourceRendering {
    private static final DecimalFormat LESS_THAN_1_BUCKET_FORMATTER;
    private static final DecimalFormat FORMATTER;

    @Override
    public String formatAmount(final long amount, final boolean withUnits) {
        return !withUnits ? format(amount) : formatWithUnits(amount);
    }

    @Override
    public Component getDisplayName(final ResourceKey resourceKey) {
        if (resourceKey instanceof ChemicalResource(Chemical chemical)) {
            return chemical.getTextComponent();
        }
        return Component.empty();
    }

    @Override
    public List<Component> getTooltip(final ResourceKey resourceKey) {
        if (resourceKey instanceof ChemicalResource(Chemical chemical)) {
            return List.of(chemical.getTextComponent());
        }
        return Collections.emptyList();
    }

    @Override
    public void render(final ResourceKey resourceKey, final GuiGraphics graphics, final int x, final int y) {
        if (resourceKey instanceof ChemicalResource(Chemical chemical)) {
            ChemicalRenderer.render(graphics.pose(), x, y, chemical);
        }
    }

    @Override
    public void render(final ResourceKey resourceKey,
                       final PoseStack poseStack,
                       final MultiBufferSource multiBufferSource,
                       final int light,
                       final Level level) {
        if (resourceKey instanceof ChemicalResource(Chemical chemical)) {
            ChemicalRenderer.render(poseStack, multiBufferSource, light, chemical);
        }
    }

    private static String formatWithUnits(final long mb) {
        final double buckets = convertToBuckets(mb);
        if (buckets >= 1) {
            return IdentifierUtil.formatWithUnits((long) Math.floor(buckets));
        } else {
            return LESS_THAN_1_BUCKET_FORMATTER.format(buckets);
        }
    }

    public static String format(final long mb) {
        final double buckets = convertToBuckets(mb);
        return FORMATTER.format(buckets);
    }

    private static double convertToBuckets(final long mb) {
        return mb / (double) FluidType.BUCKET_VOLUME;
    }

    static {
        LESS_THAN_1_BUCKET_FORMATTER = new DecimalFormat("0.#", DecimalFormatSymbols.getInstance(Locale.US));
        FORMATTER = new DecimalFormat("#,###.#", DecimalFormatSymbols.getInstance(Locale.US));
    }
}
