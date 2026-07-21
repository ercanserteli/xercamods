package xerca.xercaomnichest.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercaomnichest.Mod;

@net.neoforged.fml.common.Mod("xercaomnichest_tests")
public class OmniChestTests {
    public OmniChestTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            OmniChestGameTests tests = new OmniChestGameTests();
            helper.register(Mod.id("omni_chest_recipe"), tests::omniChestRecipeCraftsFromAmethystEyesAndEnderChest);
            helper.register(Mod.id("shared_inventory"), tests::omniChestInventoryIsSharedAcrossPlacedChests);
            helper.register(Mod.id("active_chest_tracking"), tests::omniChestTracksDifferentActiveChestsPerPlayer);
            helper.register(Mod.id("saved_data_roundtrip"), tests::omniChestSavedDataRoundTripsStoredItems);
            helper.register(Mod.id("legacy_migration"), tests::omniChestLegacySavedDataFileMigratesToNamespacedStorage);
            helper.register(Mod.id("mining_speed"), tests::omniChestBreaksAtPickaxeSpeedLikeEnderChest);
            helper.register(Mod.id("silk_touch_loot"), tests::omniChestLootRequiresSilkTouchForFullBlockDrop);
            helper.register(Mod.id("waterlogged_placement"), tests::omniChestCanBePlacedWaterlogged);
            helper.register(Mod.id("use_opens_menu"), tests::omniChestUseItemOpensMenuAndTracksActiveChest);
            helper.register(Mod.id("blocked_no_menu"), tests::blockedOmniChestDoesNotOpenMenu);
        });
    }
}
