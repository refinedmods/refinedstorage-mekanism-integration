package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.api.grid.operations.GridOperations;
import com.refinedmods.refinedstorage.api.grid.operations.GridOperationsImpl;
import com.refinedmods.refinedstorage.api.grid.view.GridResource;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.root.RootStorage;
import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.api.storage.StorageType;
import com.refinedmods.refinedstorage.common.api.support.resource.PlatformResourceKey;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceType;
import com.refinedmods.refinedstorage.common.storage.SameTypeStorageType;
import com.refinedmods.refinedstorage.mekanism.grid.ChemicalGridResourceFactory;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import mekanism.api.chemical.Chemical;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;

import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationIdentifier;
import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationTranslation;

public enum ChemicalResourceType implements ResourceType {
    INSTANCE;

    public static final MapCodec<ChemicalResource> MAP_CODEC = RecordCodecBuilder.mapCodec(ins -> ins.group(
        Chemical.CODEC.fieldOf("chemical").forGetter(ChemicalResource::chemical)
    ).apply(ins, ChemicalResource::new));
    public static final Codec<ChemicalResource> CODEC = MAP_CODEC.codec();
    public static final StreamCodec<RegistryFriendlyByteBuf, ChemicalResource> STREAM_CODEC = StreamCodec.composite(
        Chemical.STREAM_CODEC, ChemicalResource::chemical,
        ChemicalResource::new
    );
    public static final StorageType STORAGE_TYPE = new SameTypeStorageType<>(
        CODEC,
        resource -> resource instanceof ChemicalResource(Chemical chemical)
            && !chemical.hasAttributesWithValidation(),
        ChemicalResource.class::cast,
        Platform.INSTANCE.getBucketAmount(),
        Platform.INSTANCE.getBucketAmount() * 16
    );

    private static final MutableComponent TITLE = createMekanismIntegrationTranslation(
        "misc",
        "resource_type.chemical"
    );
    private static final ResourceLocation SPRITE = createMekanismIntegrationIdentifier("chemical_resource_type");

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public MapCodec<PlatformResourceKey> getMapCodec() {
        return (MapCodec) MAP_CODEC;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public StreamCodec<RegistryFriendlyByteBuf, PlatformResourceKey> getStreamCodec() {
        return (StreamCodec) STREAM_CODEC;
    }

    @Override
    public MutableComponent getTitle() {
        return TITLE;
    }

    @Override
    public ResourceLocation getSprite() {
        return SPRITE;
    }

    @Override
    public long normalizeAmount(final double amount) {
        return (long) (amount * Platform.INSTANCE.getBucketAmount());
    }

    @Override
    public double getDisplayAmount(final long amount) {
        return amount / (double) Platform.INSTANCE.getBucketAmount();
    }

    @Override
    public Optional<GridResource> toGridResource(final ResourceKey resourceKey, final boolean autocraftable) {
        return ChemicalGridResourceFactory.INSTANCE.apply(resourceKey, autocraftable);
    }

    @Override
    public long getInterfaceExportLimit() {
        return Platform.INSTANCE.getBucketAmount() * 16;
    }

    @Override
    public GridOperations createGridOperations(final RootStorage rootStorage, final Actor actor) {
        return new GridOperationsImpl(
            rootStorage,
            actor,
            resource -> Long.MAX_VALUE,
            Platform.INSTANCE.getBucketAmount()
        );
    }
}
