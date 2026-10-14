package com.refinedmods.refinedstorage.mekanism.externalstorage;

import com.refinedmods.refinedstorage.api.core.Action;
import com.refinedmods.refinedstorage.api.resource.ResourceAmount;
import com.refinedmods.refinedstorage.api.resource.ResourceKey;
import com.refinedmods.refinedstorage.api.storage.Actor;
import com.refinedmods.refinedstorage.api.storage.external.ExternalStorageProvider;
import com.refinedmods.refinedstorage.common.support.resource.ItemResource;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import javax.annotation.Nullable;

import mekanism.api.inventory.qio.IQIOComponent;
import mekanism.api.inventory.qio.IQIOFrequency;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.entity.BlockEntity;

import static com.refinedmods.refinedstorage.mekanism.ChemicalUtil.toMekanismAction;

class QioExternalStorageProvider implements ExternalStorageProvider {
    private final ServerLevel level;
    private final BlockPos pos;
    @Nullable
    private BlockEntity blockEntity;

    QioExternalStorageProvider(final ServerLevel level, final BlockPos pos) {
        this.level = level;
        this.pos = pos;
    }

    @Override
    public Iterator<ResourceAmount> iterator() {
        final IQIOFrequency frequency = getFrequency();
        if (frequency == null) {
            return Collections.emptyIterator();
        }
        final List<ResourceAmount> stored = new ArrayList<>();
        frequency.forAllHashedStored((type, amount) -> {
            if (amount > 0) {
                stored.add(new ResourceAmount(ItemResource.ofItemStack(type.getInternalStack()), amount));
            }
        });
        return stored.iterator();
    }

    @Override
    public long extract(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0;
        }
        final IQIOFrequency frequency = getFrequency();
        if (frequency == null) {
            return 0;
        }
        return frequency.massExtract(itemResource.toItemStack(), amount, toMekanismAction(action));
    }

    @Override
    public long insert(final ResourceKey resource, final long amount, final Action action, final Actor actor) {
        if (!(resource instanceof ItemResource itemResource)) {
            return 0;
        }
        final IQIOFrequency frequency = getFrequency();
        if (frequency == null) {
            return 0;
        }
        return frequency.massInsert(itemResource.toItemStack(), amount, toMekanismAction(action));
    }

    @Nullable
    private IQIOFrequency getFrequency() {
        if (blockEntity == null || blockEntity.isRemoved()) {
            blockEntity = level.isLoaded(pos) ? level.getBlockEntity(pos) : null;
        }
        return blockEntity instanceof IQIOComponent component ? component.getQIOFrequency() : null;
    }
}
