package com.refinedmods.refinedstorage.mekanism;

import com.refinedmods.refinedstorage.common.api.RefinedStorageApi;
import com.refinedmods.refinedstorage.common.content.BlockConstants;
import com.refinedmods.refinedstorage.common.content.ExtendedMenuTypeFactory;
import com.refinedmods.refinedstorage.common.storage.StorageContainerUpgradeRecipe;
import com.refinedmods.refinedstorage.common.storage.StorageContainerUpgradeRecipeSerializer;
import com.refinedmods.refinedstorage.common.support.SimpleItem;
import com.refinedmods.refinedstorage.mekanism.autocrafting.ChemicalPatternProviderExternalPatternSinkFactory;
import com.refinedmods.refinedstorage.mekanism.content.BlockEntities;
import com.refinedmods.refinedstorage.mekanism.content.Blocks;
import com.refinedmods.refinedstorage.mekanism.content.Items;
import com.refinedmods.refinedstorage.mekanism.content.Menus;
import com.refinedmods.refinedstorage.mekanism.exporter.ChemicalExporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.mekanism.externalstorage.ChemicalPlatformExternalStorageProviderFactory;
import com.refinedmods.refinedstorage.mekanism.grid.ChemicalGridExtractionStrategy;
import com.refinedmods.refinedstorage.mekanism.grid.ChemicalGridInsertionStrategy;
import com.refinedmods.refinedstorage.mekanism.importer.ChemicalImporterTransferStrategyFactory;
import com.refinedmods.refinedstorage.mekanism.recipemod.EmiChemicalResourceModIngredientConverter;
import com.refinedmods.refinedstorage.mekanism.recipemod.JeiChemicalRecipeModIngredientConverter;
import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageBlockBlockItem;
import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageBlockProvider;
import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageDiskItem;
import com.refinedmods.refinedstorage.mekanism.storage.ChemicalStorageVariant;
import com.refinedmods.refinedstorage.mekanism.storagemonitor.ChemicalStorageMonitorInsertionStrategy;
import com.refinedmods.refinedstorage.neoforge.api.RefinedStorageNeoForgeApi;

import java.util.Set;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.registries.DeferredRegister;

import static com.refinedmods.refinedstorage.mekanism.MekanismIntegrationIdentifierUtil.createMekanismIntegrationIdentifier;

@Mod(MekanismIntegrationIdentifierUtil.MOD_ID)
public final class ModInitializer {
    private static final ResourceLocation CHEMICAL_ID = createMekanismIntegrationIdentifier("chemical");

    private static final Config CONFIG = new Config();

    public ModInitializer(final IEventBus eventBus, final ModContainer modContainer) {
        modContainer.registerConfig(ModConfig.Type.COMMON, CONFIG.getSpec());
        if (FMLEnvironment.dist == Dist.CLIENT) {
            eventBus.addListener(ClientModInitializer::onClientSetup);
            eventBus.addListener(ClientModInitializer::onRegisterMenuScreens);
            modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
        }
        eventBus.addListener(this::setup);
        eventBus.addListener(this::registerCreativeModeTabListener);
        eventBus.addListener(this::registerCapabilities);

        final DeferredRegister<Item> itemRegistry = DeferredRegister.create(
            BuiltInRegistries.ITEM,
            MekanismIntegrationIdentifierUtil.MOD_ID
        );
        registerItems(itemRegistry);
        itemRegistry.register(eventBus);

        final DeferredRegister<Block> blockRegistry = DeferredRegister.create(
            BuiltInRegistries.BLOCK,
            MekanismIntegrationIdentifierUtil.MOD_ID
        );
        registerBlocks(blockRegistry);
        blockRegistry.register(eventBus);

        final DeferredRegister<BlockEntityType<?>> blockEntityRegistry = DeferredRegister.create(
            BuiltInRegistries.BLOCK_ENTITY_TYPE,
            MekanismIntegrationIdentifierUtil.MOD_ID
        );
        registerBlockEntities(blockEntityRegistry);
        blockEntityRegistry.register(eventBus);

        final DeferredRegister<MenuType<?>> menuRegistry = DeferredRegister.create(
            BuiltInRegistries.MENU,
            MekanismIntegrationIdentifierUtil.MOD_ID
        );
        final ExtendedMenuTypeFactory extendedMenuTypeFactory = new ExtendedMenuTypeFactory() {
            @Override
            public <T extends AbstractContainerMenu, D> MenuType<T> create(final MenuSupplier<T, D> supplier,
                                                                           final StreamCodec<RegistryFriendlyByteBuf, D>
                                                                               streamCodec) {
                return IMenuTypeExtension.create((syncId, inventory, buf) -> {
                    final D data = streamCodec.decode(buf);
                    return supplier.create(syncId, inventory, data);
                });
            }
        };
        registerMenus(menuRegistry, extendedMenuTypeFactory);
        menuRegistry.register(eventBus);

        final DeferredRegister<RecipeSerializer<?>> recipeSerializerRegistry = DeferredRegister.create(
            BuiltInRegistries.RECIPE_SERIALIZER,
            MekanismIntegrationIdentifierUtil.MOD_ID
        );
        registerRecipeSerializers(recipeSerializerRegistry);
        recipeSerializerRegistry.register(eventBus);
    }

    private static void registerBlocks(final DeferredRegister<Block> registry) {
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            Blocks.setChemicalStorageBlock(
                variant,
                registry.register(variant.getStorageBlockId().getPath(),
                    () -> RefinedStorageApi.INSTANCE.createStorageBlock(BlockConstants.PROPERTIES,
                        new ChemicalStorageBlockProvider(variant)
                    )));
        }
    }

    private static void registerItems(final DeferredRegister<Item> registry) {
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            if (variant.getStoragePartId() != null) {
                Items.setChemicalStoragePart(
                    variant,
                    registry.register(variant.getStoragePartId().getPath(), SimpleItem::new)
                );
            }
            Items.setChemicalStorageDisk(
                variant,
                registry.register(variant.getStorageDiskId().getPath(), () -> new ChemicalStorageDiskItem(
                    RefinedStorageApi.INSTANCE.getStorageContainerItemHelper(),
                    variant
                ))
            );
            Items.setChemicalStorageBlock(
                variant,
                registry.register(variant.getStorageBlockId().getPath(), () -> new ChemicalStorageBlockBlockItem(
                    Blocks.getChemicalStorageBlock(variant),
                    variant
                ))
            );
        }
    }

    @SuppressWarnings("DataFlowIssue")
    private static void registerBlockEntities(final DeferredRegister<BlockEntityType<?>> blockEntityRegistry) {
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            BlockEntities.setChemicalStorageBlock(
                variant,
                blockEntityRegistry.register(variant.getStorageBlockId().getPath(), () -> new BlockEntityType<>(
                    (pos, state) -> RefinedStorageApi.INSTANCE.createStorageBlockEntity(
                        pos,
                        state,
                        new ChemicalStorageBlockProvider(variant)
                    ),
                    Set.of(Blocks.getChemicalStorageBlock(variant)),
                    null
                ))
            );
        }
    }

    private static void registerMenus(final DeferredRegister<MenuType<?>> menuRegistry,
                                      final ExtendedMenuTypeFactory extendedMenuTypeFactory) {
        Menus.setChemicalStorage(menuRegistry.register(
            "chemical_storage_block",
            () -> extendedMenuTypeFactory.create((syncId, playerInventory, data) ->
                RefinedStorageApi.INSTANCE.createStorageBlockContainerMenu(
                    syncId,
                    playerInventory.player,
                    data,
                    ChemicalResourceFactory.INSTANCE,
                    Menus.getChemicalStorage()), RefinedStorageApi.INSTANCE.getStorageBlockDataStreamCodec())));
    }

    private static void registerRecipeSerializers(final DeferredRegister<RecipeSerializer<?>> registry) {
        registry.register(
            "chemical_storage_disk_upgrade",
            () -> new StorageContainerUpgradeRecipeSerializer<>(
                ChemicalStorageVariant.values(),
                to -> new StorageContainerUpgradeRecipe<>(
                    ChemicalStorageVariant.values(), to, Items::getChemicalStorageDisk
                )
            )
        );
        registry.register(
            "chemical_storage_block_upgrade",
            () -> new StorageContainerUpgradeRecipeSerializer<>(
                ChemicalStorageVariant.values(),
                to -> new StorageContainerUpgradeRecipe<>(
                    ChemicalStorageVariant.values(), to, Items::getChemicalStorageBlock
                )
            )
        );
    }

    private void setup(final FMLCommonSetupEvent e) {
        RefinedStorageApi.INSTANCE.getResourceTypeRegistry().register(CHEMICAL_ID, ChemicalResourceType.INSTANCE);
        RefinedStorageApi.INSTANCE.getAlternativeResourceFactories().add(ChemicalResourceFactory.INSTANCE);
        RefinedStorageApi.INSTANCE.getStorageTypeRegistry().register(CHEMICAL_ID, ChemicalResourceType.STORAGE_TYPE);
        RefinedStorageApi.INSTANCE.addGridInsertionStrategyFactory(ChemicalGridInsertionStrategy::new);
        RefinedStorageApi.INSTANCE.addGridExtractionStrategyFactory(ChemicalGridExtractionStrategy::new);
        RefinedStorageApi.INSTANCE.addStorageMonitorInsertionStrategy(new ChemicalStorageMonitorInsertionStrategy());
        RefinedStorageApi.INSTANCE.addResourceContainerInsertStrategy(new ChemicalResourceContainerInsertStrategy());
        RefinedStorageApi.INSTANCE.getImporterTransferStrategyRegistry().register(
            CHEMICAL_ID,
            new ChemicalImporterTransferStrategyFactory()
        );
        RefinedStorageApi.INSTANCE.getExporterTransferStrategyRegistry().register(
            CHEMICAL_ID,
            new ChemicalExporterTransferStrategyFactory()
        );
        RefinedStorageApi.INSTANCE.addExternalStorageProviderFactory(
            new ChemicalPlatformExternalStorageProviderFactory()
        );
        RefinedStorageApi.INSTANCE.addPatternProviderExternalPatternSinkFactory(
            new ChemicalPatternProviderExternalPatternSinkFactory()
        );
        if (ModList.get().isLoaded("emi")) {
            RefinedStorageApi.INSTANCE.addIngredientConverter(new EmiChemicalResourceModIngredientConverter());
        } else if (ModList.get().isLoaded("jei")) {
            RefinedStorageApi.INSTANCE.addIngredientConverter(new JeiChemicalRecipeModIngredientConverter());
        }
    }

    private void registerCreativeModeTabListener(final BuildCreativeModeTabContentsEvent e) {
        final ResourceKey<CreativeModeTab> creativeModeTab = ResourceKey.create(
            Registries.CREATIVE_MODE_TAB,
            RefinedStorageApi.INSTANCE.getCreativeModeTabId()
        );
        if (!e.getTabKey().equals(creativeModeTab)) {
            return;
        }
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            if (variant.getStoragePartId() != null) {
                e.accept(Items.getChemicalStoragePart(variant));
            }
        }
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            e.accept(Items.getChemicalStorageDisk(variant));
        }
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            e.accept(Items.getChemicalStorageBlock(variant));
        }
    }

    private void registerCapabilities(final RegisterCapabilitiesEvent event) {
        for (final ChemicalStorageVariant variant : ChemicalStorageVariant.values()) {
            event.registerBlockEntity(
                RefinedStorageNeoForgeApi.INSTANCE.getNetworkNodeContainerProviderCapability(),
                BlockEntities.getChemicalStorageBlock(variant),
                (be, side) -> be.getContainerProvider()
            );
        }
    }

    public static Config getConfig() {
        return CONFIG;
    }
}
