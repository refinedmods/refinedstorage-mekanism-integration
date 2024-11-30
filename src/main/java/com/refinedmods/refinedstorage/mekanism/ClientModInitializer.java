package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.common.api.RefinedStorageClientApi;
import com.refinedmods.refinedstorage.mekanism.content.Items;
import com.refinedmods.refinedstorage.mekanism.content.Menus;
import com.refinedmods.refinedstorage.mekanism.grid.ChemicalGridInsertionHint;
import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageVariant;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;

import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationIdentifier;

public final class ClientModInitializer {
    private ClientModInitializer() {
    }

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent e) {
        RefinedStorageClientApi.INSTANCE.registerResourceRendering(
            ChemicalResource.class,
            new ChemicalResourceRendering()
        );
        RefinedStorageClientApi.INSTANCE.addAlternativeGridInsertionHint(new ChemicalGridInsertionHint());
        final ResourceLocation diskModel = createMekanismIntegrationIdentifier("block/disk/chemical_disk");
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            RefinedStorageClientApi.INSTANCE.registerDiskModel(
                Items.getChemicalStorageDisk(variant),
                diskModel
            );
        }
    }

    @SubscribeEvent
    public static void onRegisterMenuScreens(final RegisterMenuScreensEvent e) {
        e.register(
            Menus.getChemicalStorage(),
            new MenuScreens.ScreenConstructor<AbstractContainerMenu, AbstractContainerScreen<AbstractContainerMenu>>() {
                @Override
                public AbstractContainerScreen<AbstractContainerMenu> create(final AbstractContainerMenu menu,
                                                                             final Inventory inventory,
                                                                             final Component title) {
                    return RefinedStorageClientApi.INSTANCE.createStorageBlockScreen(menu, inventory, title,
                        ChemicalResource.class);
                }
            }
        );
    }
}
