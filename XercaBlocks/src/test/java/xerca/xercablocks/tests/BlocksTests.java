package xerca.xercablocks.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercablocks.Mod;

@net.neoforged.fml.common.Mod("xercablocks_tests")
public class BlocksTests {
    public BlocksTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            BlocksGameTests tests = new BlocksGameTests();
            helper.register(Mod.id("rope_recipe_crafts_from_three_string"), tests::ropeRecipeCraftsFromThreeString);
            helper.register(Mod.id("leather_block_recipe_crafts_from_nine_leather"), tests::leatherBlockRecipeCraftsFromNineLeather);
            helper.register(Mod.id("straw_block_recipe_crafts_from_nine_sugar_cane"), tests::strawBlockRecipeCraftsFromNineSugarCane);
            helper.register(Mod.id("black_terratile_recipe_crafts_from_black_terracotta"), tests::blackTerratileRecipeCraftsFromBlackTerracotta);
            helper.register(Mod.id("black_terratile_slab_recipe_crafts_six_slabs"), tests::blackTerratileSlabRecipeCraftsSixSlabs);
            helper.register(Mod.id("black_terratile_stairs_recipe_crafts_four_stairs"), tests::blackTerratileStairsRecipeCraftsFourStairs);
            helper.register(Mod.id("black_terratile_stonecutting_recipes_produce_tile_slab_and_stairs"), tests::blackTerratileStonecuttingRecipesProduceTileSlabAndStairs);
            helper.register(Mod.id("carving_station_recipe_cuts_oak_log_into_carved_oak"), tests::carvingStationRecipeCutsOakLogIntoCarvedOak);
            helper.register(Mod.id("crafting_carving_station_consumes_shears"), tests::craftingCarvingStationConsumesShears);
            helper.register(Mod.id("bookcase_menu_rejects_non_book_items"), tests::bookcaseMenuRejectsNonBookItems);
            helper.register(Mod.id("bookcase_state_tracks_stored_books"), tests::bookcaseStateTracksStoredBooks);
            helper.register(Mod.id("terratile_breaks_faster_with_pickaxe_than_by_hand"), tests::terratileBreaksFasterWithPickaxeThanByHand);
            helper.register(Mod.id("terratile_slab_breaks_faster_with_pickaxe_than_by_hand"), tests::terratileSlabBreaksFasterWithPickaxeThanByHand);
            helper.register(Mod.id("terratile_stairs_breaks_faster_with_pickaxe_than_by_hand"), tests::terratileStairsBreaksFasterWithPickaxeThanByHand);
            helper.register(Mod.id("double_terratile_slab_drops_two_items"), tests::doubleTerratileSlabDropsTwoItems);
            helper.register(Mod.id("black_terratile_loot_drops_itself"), tests::blackTerratileLootDropsItself);
            helper.register(Mod.id("black_terratile_stairs_loot_drops_itself"), tests::blackTerratileStairsLootDropsItself);
            helper.register(Mod.id("carved_wood_breaks_faster_with_axe_than_by_hand"), tests::carvedWoodBreaksFasterWithAxeThanByHand);
            helper.register(Mod.id("carved_wood_drops_itself_when_broken"), tests::carvedWoodDropsItselfWhenBroken);
            helper.register(Mod.id("carved_acacia_lets_light_through_and_culls_like_agrate"), tests::carvedAcaciaLetsLightThroughAndCullsLikeAGrate);
            helper.register(Mod.id("rope_has_click_box_matching_its_model"), tests::ropeHasClickBoxMatchingItsModel);
            helper.register(Mod.id("rope_drops_itself_when_broken"), tests::ropeDropsItselfWhenBroken);
            helper.register(Mod.id("rope_placement_connects_to_supporting_blocks_and_neighboring_rope"), tests::ropePlacementConnectsToSupportingBlocksAndNeighboringRope);
            helper.register(Mod.id("rope_is_tagged_climbable_and_players_recognize_it"), tests::ropeIsTaggedClimbableAndPlayersRecognizeIt);
            helper.register(Mod.id("rope_pushes_attached_blocks_because_it_is_sticky"), tests::ropePushesAttachedBlocksBecauseItIsSticky);
            helper.register(Mod.id("sticky_piston_retracts_rope_and_its_attached_block"), tests::stickyPistonRetractsRopeAndItsAttachedBlock);
            helper.register(Mod.id("bookcase_drops_block_and_stored_books_when_broken"), tests::bookcaseDropsBlockAndStoredBooksWhenBroken);
            helper.register(Mod.id("bookcase_persists_stored_books_across_block_entity_reload"), tests::bookcasePersistsStoredBooksAcrossBlockEntityReload);
            helper.register(Mod.id("bookcase_comparator_output_tracks_stored_books"), tests::bookcaseComparatorOutputTracksStoredBooks);
            helper.register(Mod.id("carving_recipes_do_not_load_as_stonecutting_recipes"), tests::carvingRecipesDoNotLoadAsStonecuttingRecipes);
            helper.register(Mod.id("carving_output_classification_routes_stripped_logs_to_carving_station"), tests::carvingOutputClassificationRoutesStrippedLogsToCarvingStation);
            helper.register(Mod.id("carving_station_menu_shows_carving_recipes_but_stonecutter_does_not"), tests::carvingStationMenuShowsCarvingRecipesButStonecutterDoesNot);
            helper.register(Mod.id("carving_station_uses_custom_menu_type"), tests::carvingStationUsesCustomMenuType);
            helper.register(Mod.id("carving_station_rejects_stonecutter_inputs_while_stonecutter_accepts_them"), tests::carvingStationRejectsStonecutterInputsWhileStonecutterAcceptsThem);
            helper.register(Mod.id("carving_station_consumes_held_item_use"), tests::carvingStationConsumesHeldItemUse);
            helper.register(Mod.id("functional_bookcase_consumes_held_item_use"), tests::functionalBookcaseConsumesHeldItemUse);
        });
    }
}
