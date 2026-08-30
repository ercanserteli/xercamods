package xerca.xercafood.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercafood.common.Mod;

@net.neoforged.fml.common.Mod("xercafood_tests")
public class FoodTests {
    public FoodTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            AdvancementGameTests advancementGameTests = new AdvancementGameTests();
            helper.register(Mod.id("tea_advancement_only_triggers_for_hot_teapots"), advancementGameTests::teaAdvancementOnlyTriggersForHotTeapots);
            helper.register(Mod.id("recipe_advancement_unlocks_tomato_slicing_recipe"), advancementGameTests::recipeAdvancementUnlocksTomatoSlicingRecipe);
            helper.register(Mod.id("advancement_triggers_for_ultimate_burger_tomato_hit_and_golden_cupcake"), advancementGameTests::advancementTriggersForUltimateBurgerTomatoHitAndGoldenCupcake);
            BlockGameTests blockGameTests = new BlockGameTests();
            helper.register(Mod.id("tomato_plant_drops_tomato_when_grown"), blockGameTests::tomatoPlantDropsTomatoWhenGrown);
            helper.register(Mod.id("rice_plant_drops_rice_seeds_when_grown"), blockGameTests::ricePlantDropsRiceSeedsWhenGrown);
            helper.register(Mod.id("rice_plant_requires_two_adjacent_water_sources_to_survive"), blockGameTests::ricePlantRequiresTwoAdjacentWaterSourcesToSurvive);
            helper.register(Mod.id("rice_plant_tick_breaks_when_support_water_drops_below_requirement"), blockGameTests::ricePlantTickBreaksWhenSupportWaterDropsBelowRequirement);
            helper.register(Mod.id("tea_plant_drops_tea_leaf_when_grown"), blockGameTests::teaPlantDropsTeaLeafWhenGrown);
            helper.register(Mod.id("vat_drops_vat"), blockGameTests::vatDropsVat);
            helper.register(Mod.id("vat_breaks_faster_with_pickaxe_than_by_hand"), blockGameTests::vatBreaksFasterWithPickaxeThanByHand);
            helper.register(Mod.id("vat_drops_itself_without_any_tool"), blockGameTests::vatDropsItselfWithoutAnyTool);
            helper.register(Mod.id("pies_break_at_cake_speed"), blockGameTests::piesBreakAtCakeSpeed);
            helper.register(Mod.id("pies_consume_by_slices_until_removed"), blockGameTests::piesConsumeBySlicesUntilRemoved);
            helper.register(Mod.id("cheese_wheel_and_pizza_consume_by_quarters"), blockGameTests::cheeseWheelAndPizzaConsumeByQuarters);
            helper.register(Mod.id("cheese_wheel_slicing_with_knife_drops_slice"), blockGameTests::cheeseWheelSlicingWithKnifeDropsSlice);
            helper.register(Mod.id("vat_milk_to_cheese_conversion_flow_works"), blockGameTests::vatMilkToCheeseConversionFlowWorks);
            helper.register(Mod.id("doner_creation_cooking_and_slicing_flow_works"), blockGameTests::donerCreationCookingAndSlicingFlowWorks);
            helper.register(Mod.id("crop_growth_and_bone_meal_behavior_for_tea_and_tomato"), blockGameTests::cropGrowthAndBoneMealBehaviorForTeaAndTomato);
            helper.register(Mod.id("short_grass_can_drop_tea_and_tomato_seeds"), blockGameTests::shortGrassCanDropTeaAndTomatoSeeds);
            helper.register(Mod.id("teapot_block_interaction_fills_cup"), blockGameTests::teapotBlockInteractionFillsCup);
            helper.register(Mod.id("hot_teapot_placement_preserves_tea_amount_and_drops_matching_hot_item"), blockGameTests::hotTeapotPlacementPreservesTeaAmountAndDropsMatchingHotItem);
            helper.register(Mod.id("cold_filled_teapot_cannot_be_placed_as_block"), blockGameTests::coldFilledTeapotCannotBePlacedAsBlock);
            FoodItemGameTests foodItemGameTests = new FoodItemGameTests();
            helper.register(Mod.id("rotten_burger_lowers_hunger_and_can_apply_poison"), foodItemGameTests::rottenBurgerLowersHungerAndCanApplyPoison);
            helper.register(Mod.id("ultimate_burger_applies_saturation_effect"), foodItemGameTests::ultimateBurgerAppliesSaturationEffect);
            helper.register(Mod.id("chorus_cupcake_teleports_player"), foodItemGameTests::chorusCupcakeTeleportsPlayer);
            helper.register(Mod.id("golden_cupcake_produces_random_outcomes"), foodItemGameTests::goldenCupcakeProducesRandomOutcomes);
            helper.register(Mod.id("teacup_sugar_levels_change_nutrition_and_effects"), foodItemGameTests::teacupSugarLevelsChangeNutritionAndEffects);
            helper.register(Mod.id("meat_foods_use_minecraft_tags"), foodItemGameTests::meatFoodsUseMinecraftTags);
            helper.register(Mod.id("tea_use_duration_and_drink_effects_are_correct"), foodItemGameTests::teaUseDurationAndDrinkEffectsAreCorrect);
            helper.register(Mod.id("tomato_projectile_hit_damages_entity"), foodItemGameTests::tomatoProjectileHitDamagesEntity);
            RecipeGameTests recipeGameTests = new RecipeGameTests();
            helper.register(Mod.id("shaped_recipe_crafts_apple_cupcake"), recipeGameTests::shapedRecipeCraftsAppleCupcake);
            helper.register(Mod.id("shapeless_recipe_crafts_cola_powder"), recipeGameTests::shapelessRecipeCraftsColaPowder);
            helper.register(Mod.id("cupcake_recipe_accepts_every_egg_variant"), recipeGameTests::cupcakeRecipeAcceptsEveryEggVariant);
            helper.register(Mod.id("fried_egg_smelting_accepts_every_egg_variant"), recipeGameTests::friedEggSmeltingAcceptsEveryEggVariant);
            helper.register(Mod.id("slicing_tomato_damages_knife"), recipeGameTests::slicingTomatoDamagesKnife);
            helper.register(Mod.id("repairing_knives_has_no_crafting_recipe"), recipeGameTests::repairingKnivesHasNoCraftingRecipe);
            helper.register(Mod.id("smelting_recipe_cooks_raw_patty"), recipeGameTests::smeltingRecipeCooksRawPatty);
            helper.register(Mod.id("smoking_recipe_cooks_raw_sausage"), recipeGameTests::smokingRecipeCooksRawSausage);
            helper.register(Mod.id("campfire_recipe_loads_and_cooks_pizza_variant"), recipeGameTests::campfireRecipeLoadsAndCooksPizzaVariant);
            helper.register(Mod.id("pizza_recipe_works_end_to_end"), recipeGameTests::pizzaRecipeWorksEndToEnd);
            helper.register(Mod.id("tea_pouring_fills_cups_and_returns_reduced_teapot"), recipeGameTests::teaPouringFillsCupsAndReturnsReducedTeapot);
            helper.register(Mod.id("tea_sugaring_increases_sugar_level"), recipeGameTests::teaSugaringIncreasesSugarLevel);
            helper.register(Mod.id("brewing_produces_textured_cola_extract_and_crafts_cola"), recipeGameTests::brewingProducesTexturedColaExtractAndCraftsCola);
            helper.register(Mod.id("tea_leaf_smelts_to_dried_tea_leaves"), recipeGameTests::teaLeafSmeltsToDriedTeaLeaves);
            helper.register(Mod.id("tea_filling_and_refilling_edge_cases_work"), recipeGameTests::teaFillingAndRefillingEdgeCasesWork);
            helper.register(Mod.id("pouring_last_cup_reverts_hot_teapot_to_empty_teapot"), recipeGameTests::pouringLastCupRevertsHotTeapotToEmptyTeapot);
            helper.register(Mod.id("sparkling_water_and_soda_cola_flows_work"), recipeGameTests::sparklingWaterAndSodaColaFlowsWork);
            VillagerTradeGameTests villagerTradeGameTests = new VillagerTradeGameTests();
            helper.register(Mod.id("farmer_level_one_tag_contains_food_trades"), villagerTradeGameTests::farmerLevelOneTagContainsFoodTrades);
        });
    }
}
