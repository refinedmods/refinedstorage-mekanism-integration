package com.refinedmods.refinedstorage.mekanism.storage;

import com.refinedmods.refinedstorage.common.Platform;
import com.refinedmods.refinedstorage.common.storage.StorageVariant;
import com.refinedmods.refinedstorage.mekanism.Items;

import javax.annotation.Nullable;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.item.Item;

import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationIdentifier;

public enum ChemicalStorageVariant implements StringRepresentable, StorageVariant {
    SIXTY_FOUR_B("64b", 64L),
    TWO_HUNDRED_FIFTY_SIX_B("256b", 256L),
    THOUSAND_TWENTY_FOUR_B("1024b", 1024L),
    EIGHT_THOUSAND_NINETY_TWO_B("8192b", 8192L),
    CREATIVE("creative", null);

    private final String name;
    private final ResourceLocation storageDiskId;
    @Nullable
    private final ResourceLocation storagePartId;
    @Nullable
    private final Long capacityInBuckets;

    ChemicalStorageVariant(final String name, @Nullable final Long capacityInBuckets) {
        this.name = name;
        this.storagePartId = capacityInBuckets != null
            ? createMekanismIntegrationIdentifier(name + "_chemical_storage_part")
            : null;
        this.storageDiskId = createMekanismIntegrationIdentifier(name + "_chemical_storage_disk");
        this.capacityInBuckets = capacityInBuckets;
    }

    @Nullable
    public Long getCapacityInBuckets() {
        return capacityInBuckets;
    }

    @Override
    @Nullable
    public Long getCapacity() {
        if (capacityInBuckets == null) {
            return null;
        }
        return capacityInBuckets * Platform.INSTANCE.getBucketAmount();
    }

    @Nullable
    @Override
    public Item getStoragePart() {
        if (this == CREATIVE) {
            return null;
        }
        return Items.INSTANCE.getChemicalStoragePart(this);
    }

    public ResourceLocation getStorageDiskId() {
        return storageDiskId;
    }

    @Nullable
    public ResourceLocation getStoragePartId() {
        return storagePartId;
    }

    @Override
    public String getSerializedName() {
        return name;
    }
}
