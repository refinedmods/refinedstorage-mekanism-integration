package com.refinedmods.refinedstorage.mekanism.storage;

import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.api.storage.StorageBlockProvider;
import com.refinedmods.refinedstorage.common.api.support.resource.ResourceFactory;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceFactory;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import com.refinedmods.refinedstorage.mekanism.ModInitializer;
import com.refinedmods.refinedstorage.mekanism.content.BlockEntities;
import com.refinedmods.refinedstorage.mekanism.content.Menus;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationTranslation;

public class ChemicalStorageBlockProvider implements StorageBlockProvider {
    private final ChemicalStorageVariant variant;
    private final Component displayName;

    public ChemicalStorageBlockProvider(final ChemicalStorageVariant variant) {
        this.variant = variant;
        this.displayName = createMekanismIntegrationTranslation(
            "block",
            String.format("%s_chemical_storage_block", variant.getName())
        );
    }

    @Override
    public SerializableStorage createStorage(final Runnable runnable) {
        return ChemicalResourceType.STORAGE_TYPE.create(variant.getCapacity(), runnable);
    }

    @Override
    public Component getDisplayName() {
        return displayName;
    }

    @Override
    public long getEnergyUsage() {
        return switch (this.variant) {
            case SIXTY_FOUR_B -> ModInitializer.getConfig().getChemicalStorageBlock().get64bEnergyUsage();
            case TWO_HUNDRED_FIFTY_SIX_B -> ModInitializer.getConfig().getChemicalStorageBlock().get256bEnergyUsage();
            case THOUSAND_TWENTY_FOUR_B -> ModInitializer.getConfig().getChemicalStorageBlock().get1024bEnergyUsage();
            case EIGHT_THOUSAND_NINETY_TWO_B ->
                ModInitializer.getConfig().getChemicalStorageBlock().get8192bEnergyUsage();
            case CREATIVE -> ModInitializer.getConfig().getChemicalStorageBlock().getCreativeEnergyUsage();
        };
    }

    @Override
    public ResourceFactory getResourceFactory() {
        return ChemicalResourceFactory.INSTANCE;
    }

    @Override
    public BlockEntityType<?> getBlockEntityType() {
        return BlockEntities.getChemicalStorageBlock(variant);
    }

    @Override
    public MenuType<?> getMenuType() {
        return Menus.getChemicalStorage();
    }
}
