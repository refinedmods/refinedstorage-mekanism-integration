package com.refinedmods.refinedstorage.mekanism.chemical;

import com.refinedmods.refinedstorage.common.api.storage.AbstractStorageContainerItem;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.api.storage.StorageContainerItemHelper;
import com.refinedmods.refinedstorage.common.api.storage.StorageRepository;
import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.storage.StorageVariant;
import com.refinedmods.refinedstorage.common.storage.UpgradeableStorageContainer;
import com.refinedmods.refinedstorage.mekanism.Items;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.format;
import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationTranslation;

public class ChemicalStorageDiskItem extends AbstractStorageContainerItem implements UpgradeableStorageContainer {
    private static final Component CREATIVE_HELP = createMekanismIntegrationTranslation(
        "item",
        "creative_chemical_storage_disk.help"
    );

    private final ChemicalStorageVariant variant;
    private final Component helpText;

    public ChemicalStorageDiskItem(final StorageContainerItemHelper helper,
                                   final ChemicalStorageVariant variant) {
        super(new Item.Properties().stacksTo(1).fireResistant(), helper);
        this.variant = variant;
        this.helpText = getHelpText(variant);
    }

    private static Component getHelpText(final ChemicalStorageVariant variant) {
        if (variant.getCapacityInBuckets() == null) {
            return CREATIVE_HELP;
        }
        return createMekanismIntegrationTranslation(
            "item",
            "chemical_storage_disk.help",
            format(variant.getCapacityInBuckets())
        );
    }


    @Override
    @Nullable
    protected Long getCapacity() {
        return variant.getCapacity();
    }

    @Override
    protected String formatAmount(final long amount) {
        return ChemicalResourceRendering.format(amount);
    }

    @Override
    protected SerializableStorage createStorage(final StorageRepository storageRepository) {
        return ChemicalResourceType.STORAGE_TYPE.create(getCapacity(), storageRepository::markAsChanged);
    }

    @Override
    protected ItemStack createPrimaryDisassemblyByproduct(final int count) {
        return new ItemStack(com.refinedmods.refinedstorage.common.content.Items.INSTANCE.getStorageHousing(), count);
    }

    @Override
    @Nullable
    protected ItemStack createSecondaryDisassemblyByproduct(final int count) {
        if (variant == ChemicalStorageVariant.CREATIVE) {
            return null;
        }
        return new ItemStack(Items.INSTANCE.getChemicalStoragePart(variant), count);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(final ItemStack stack) {
        return Optional.of(new HelpTooltipComponent(helpText));
    }

    @Override
    public StorageVariant getVariant() {
        return variant;
    }

    @Override
    public void transferTo(final ItemStack from, final ItemStack to) {
        helper.markAsToTransfer(from, to);
    }
}
