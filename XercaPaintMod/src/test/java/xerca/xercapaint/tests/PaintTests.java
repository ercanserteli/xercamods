package xerca.xercapaint.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercapaint.Mod;

@net.neoforged.fml.common.Mod("xercapaint_tests")
public class PaintTests {
    public PaintTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            BaseCraftingGameTests baseCrafting = new BaseCraftingGameTests();
            helper.register(Mod.id("easel_crafts_from_sticks"), baseCrafting::easelCraftsFromSticks);
            helper.register(Mod.id("palette_crafts_from_brush_and_planks"), baseCrafting::paletteCraftsFromBrushAndPlanks);
            helper.register(Mod.id("small_canvas_crafts_from_paper_and_sticks"), baseCrafting::smallCanvasCraftsFromPaperAndSticks);

            CanvasRecipeGameTests canvasRecipe = new CanvasRecipeGameTests();
            helper.register(Mod.id("small_fresh_canvases_craft_long_tall_and_large"), canvasRecipe::smallFreshCanvasesCraftLongTallAndLarge);
            helper.register(Mod.id("small_fresh_glass_canvases_craft_long_tall_and_large"), canvasRecipe::smallFreshGlassCanvasesCraftLongTallAndLarge);
            helper.register(Mod.id("glass_canvas_crafts_from_glass_pane_and_sticks"), canvasRecipe::glassCanvasCraftsFromGlassPaneAndSticks);
            helper.register(Mod.id("painted_canvases_cannot_be_used_in_fresh_canvas_recipes"), canvasRecipe::paintedCanvasesCannotBeUsedInFreshCanvasRecipes);
            helper.register(Mod.id("foreign_tagged_fresh_canvases_can_be_used_in_fresh_canvas_recipes"), canvasRecipe::foreignTaggedFreshCanvasesCanBeUsedInFreshCanvasRecipes);

            CanvasSidesGameTests canvasSides = new CanvasSidesGameTests();
            helper.register(Mod.id("sides_layout_matches_dimensions"), canvasSides::sidesLayoutMatchesDimensions);
            helper.register(Mod.id("canvas_update_packet_round_trips_sides"), canvasSides::canvasUpdatePacketRoundTripsSides);
            helper.register(Mod.id("cloning_copies_side_pixels"), canvasSides::cloningCopiesSidePixels);
            helper.register(Mod.id("glass_flag_survives_entity_nbt_round_trip"), canvasSides::glassFlagSurvivesEntityNbtRoundTrip);

            CanvasTagCompatibilityGameTests tagCompat = new CanvasTagCompatibilityGameTests();
            helper.register(Mod.id("foreign_tag_alone_is_not_canvas_data"), tagCompat::foreignTagAloneIsNotCanvasData);
            helper.register(Mod.id("cloning_treats_foreign_tagged_fresh_canvas_as_fresh"), tagCompat::cloningTreatsForeignTaggedFreshCanvasAsFresh);
            helper.register(Mod.id("canvas_rotation_does_not_clobber_vanilla_rotation_key"), tagCompat::canvasRotationDoesNotClobberVanillaRotationKey);

            PaletteDropperGameTests paletteDropper = new PaletteDropperGameTests();
            helper.register(Mod.id("dropping_pure_black_into_custom_slot_does_not_divide_by_zero"), paletteDropper::droppingPureBlackIntoCustomSlotDoesNotDivideByZero);
            helper.register(Mod.id("dropping_single_color_keeps_exact_color"), paletteDropper::droppingSingleColorKeepsExactColor);
            helper.register(Mod.id("dropping_two_saturated_colors_mixes_predictably"), paletteDropper::droppingTwoSaturatedColorsMixesPredictably);
            helper.register(Mod.id("dropping_many_colors_always_stays_in_rgb_range"), paletteDropper::droppingManyColorsAlwaysStaysInRgbRange);

            RecipeCanvasCloningGameTests cloning = new RecipeCanvasCloningGameTests();
            helper.register(Mod.id("cloning_rejects_generation_zero_original_canvas"), cloning::cloningRejectsGenerationZeroOriginalCanvas);
            helper.register(Mod.id("cloning_stops_at_generation_three"), cloning::cloningStopsAtGenerationThree);
            helper.register(Mod.id("cloning_rejects_different_canvas_types"), cloning::cloningRejectsDifferentCanvasTypes);
            helper.register(Mod.id("cloning_rejects_mixed_materials"), cloning::cloningRejectsMixedMaterials);
            helper.register(Mod.id("cloning_glass_canvas_produces_glass_canvas"), cloning::cloningGlassCanvasProducesGlassCanvas);
            helper.register(Mod.id("signed_canvases_stack_to_sixteen_by_same_generation"), cloning::signedCanvasesStackToSixteenBySameGeneration);
            helper.register(Mod.id("cloning_consumes_fresh_canvas_and_keeps_original_as_remainder"), cloning::cloningConsumesFreshCanvasAndKeepsOriginalAsRemainder);

            RecipeFillPaletteGameTests fillPalette = new RecipeFillPaletteGameTests();
            helper.register(Mod.id("fill_palette_adds_new_basic_colors_and_preserves_custom_tag"), fillPalette::fillPaletteAddsNewBasicColorsAndPreservesCustomTag);
            helper.register(Mod.id("fill_palette_rejects_already_present_dye"), fillPalette::fillPaletteRejectsAlreadyPresentDye);
            helper.register(Mod.id("fill_palette_rejects_unknown_items_and_no_dye"), fillPalette::fillPaletteRejectsUnknownItemsAndNoDye);

            EaselTests easel = new EaselTests();
            helper.register(Mod.id("placing_easel_faces_player_from_all_eight_directions"), easel::placingEaselFacesPlayerFromAllEightDirections);
            helper.register(Mod.id("right_click_with_canvas_empty_hand_and_palette_has_expected_modes"), easel::rightClickWithCanvasEmptyHandAndPaletteHasExpectedModes);
            helper.register(Mod.id("second_player_cannot_steal_edit_lock_and_can_break_and_drop_both_items"), easel::secondPlayerCannotStealEditLockAndCanBreakAndDropBothItems);
            helper.register(Mod.id("easel_can_be_broken_by_explosion"), easel::easelCanBeBrokenByExplosion);
            helper.register(Mod.id("invulnerable_tagged_easel_ignores_player_and_explosion_damage"), easel::invulnerableTaggedEaselIgnoresPlayerAndExplosionDamage);
        });
    }
}
