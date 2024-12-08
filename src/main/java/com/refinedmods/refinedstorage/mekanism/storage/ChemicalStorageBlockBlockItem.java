package com.refinedmods.refinedstorage.mekanism.storage;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.common.api.storage.AbstractStorageContainerBlockItem;
import com.refinedmods.refinedstorage.common.api.storage.SerializableStorage;
import com.refinedmods.refinedstorage.common.api.storage.StorageRepository;
import com.refinedmods.refinedstorage.common.api.support.HelpTooltipComponent;
import com.refinedmods.refinedstorage.common.content.Blocks;
import com.refinedmods.refinedstorage.common.storage.StorageVariant;
import com.refinedmods.refinedstorage.common.storage.UpgradeableStorageContainer;
import com.refinedmods.refinedstorage.mekanism.ChemicalResource;
import com.refinedmods.refinedstorage.mekanism.ChemicalResourceType;
import com.refinedmods.refinedstorage.mekanism.content.Items;

import java.util.Optional;
import javax.annotation.Nullable;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import static com.refinedmods.refinedstorage.common.util.IdentifierUtil.format;
import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationTranslation;

public class ChemicalStorageBlockBlockItem extends AbstractStorageContainerBlockItem
    implements UpgradeableStorageContainer {
    private static final Component CREATIVE_HELP =
        createMekanismIntegrationTranslation("item", "creative_chemical_storage_block.help");

    private final ChemicalStorageVariant variant;
    private final Component helpText;

    public ChemicalStorageBlockBlockItem(final Block block, final ChemicalStorageVariant variant) {
        super(
            block,
            new Item.Properties().stacksTo(1).fireResistant(),
            RefinedStorageApi.INSTANCE.getStorageContainerItemHelper()
        );
        this.variant = variant;
        this.helpText = getHelpText(variant);
    }

    private static Component getHelpText(final ChemicalStorageVariant variant) {
        if (variant.getCapacityInBuckets() == null) {
            return CREATIVE_HELP;
        }
        return createMekanismIntegrationTranslation(
            "item",
            "chemical_storage_block.help",
            format(variant.getCapacityInBuckets())
        );
    }

    @Nullable
    @Override
    protected Long getCapacity() {
        return variant.getCapacity();
    }

    @Override
    protected String formatAmount(final long amount) {
        return RefinedStorageClientApi.INSTANCE.getResourceRendering(ChemicalResource.class).formatAmount(amount);
    }

    @Override
    protected SerializableStorage createStorage(final StorageRepository storageRepository) {
        return createStorage(variant, storageRepository::markAsChanged);
    }

    static SerializableStorage createStorage(final ChemicalStorageVariant variant, final Runnable listener) {
        return ChemicalResourceType.STORAGE_TYPE.create(variant.getCapacity(), listener);
    }

    @Override
    protected ItemStack createPrimaryDisassemblyByproduct(final int count) {
        return new ItemStack(Blocks.INSTANCE.getMachineCasing(), count);
    }

    @Override
    @Nullable
    protected ItemStack createSecondaryDisassemblyByproduct(final int count) {
        if (variant == ChemicalStorageVariant.CREATIVE) {
            return null;
        }
        return new ItemStack(Items.getChemicalStoragePart(variant), count);
    }

    @Override
    protected boolean placeBlock(final BlockPlaceContext ctx, final BlockState state) {
        if (ctx.getPlayer() instanceof ServerPlayer serverPlayer && !(RefinedStorageApi.INSTANCE.canPlaceNetworkNode(
            serverPlayer,
            ctx.getLevel(),
            ctx.getClickedPos(),
            state))) {
            return false;
        }
        return super.placeBlock(ctx, state);
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
