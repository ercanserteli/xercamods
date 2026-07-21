package xerca.xercatools.tests;

import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.RegisterEvent;
import xerca.xercatools.Mod;

@net.neoforged.fml.common.Mod("xercatools_tests")
public class ToolsTests {
    public ToolsTests(IEventBus modEventBus) {
        modEventBus.addListener(this::onRegisterTestFunctions);
    }

    private void onRegisterTestFunctions(RegisterEvent event) {
        event.register(Registries.TEST_FUNCTION, helper -> {
            ConfettiBallGameTests confettiBallGameTests = new ConfettiBallGameTests();
            helper.register(Mod.id("player_use_spawns_confetti_ball_and_consumes_one_item"), confettiBallGameTests::playerUseSpawnsConfettiBallAndConsumesOneItem);
            helper.register(Mod.id("dispenser_use_spawns_confetti_ball_without_synched_data_crash"), confettiBallGameTests::dispenserUseSpawnsConfettiBallWithoutSynchedDataCrash);
            helper.register(Mod.id("confetti_recipe_crafts_twelve_pieces_from_paper_and_cmy_dyes"), confettiBallGameTests::confettiRecipeCraftsTwelvePiecesFromPaperAndCmyDyes);
            helper.register(Mod.id("confetti_ball_recipe_crafts_two_balls_from_cross_of_confetti_and_gunpowder"), confettiBallGameTests::confettiBallRecipeCraftsTwoBallsFromCrossOfConfettiAndGunpowder);
            helper.register(Mod.id("confetti_ball_recipe_rejects_missing_confetti_arm"), confettiBallGameTests::confettiBallRecipeRejectsMissingConfettiArm);
            helper.register(Mod.id("confetti_use_consumes_one_item_for_survival_players"), confettiBallGameTests::confettiUseConsumesOneItemForSurvivalPlayers);
            helper.register(Mod.id("confetti_use_does_not_consume_item_for_creative_players"), confettiBallGameTests::confettiUseDoesNotConsumeItemForCreativePlayers);
            helper.register(Mod.id("confetti_dispense_behavior_consumes_one_item"), confettiBallGameTests::confettiDispenseBehaviorConsumesOneItem);
            FlaskAndLauncherGameTests flaskAndLauncherGameTests = new FlaskAndLauncherGameTests();
            helper.register(Mod.id("flask_max_charges_base_is16"), flaskAndLauncherGameTests::flaskMaxChargesBaseIs16);
            helper.register(Mod.id("flask_has_drink_consumable_so_drinking_makes_sound"), flaskAndLauncherGameTests::flaskHasDrinkConsumableSoDrinkingMakesSound);
            helper.register(Mod.id("flask_capacity_enchantment_doubles_max_charges"), flaskAndLauncherGameTests::flaskCapacityEnchantmentDoublesMaxCharges);
            helper.register(Mod.id("flask_use_duration_base_is32"), flaskAndLauncherGameTests::flaskUseDurationBaseIs32);
            helper.register(Mod.id("flask_chug_level1_decreases_use_duration"), flaskAndLauncherGameTests::flaskChugLevel1DecreasesUseDuration);
            helper.register(Mod.id("flask_chug_level2_decreases_use_duration_further"), flaskAndLauncherGameTests::flaskChugLevel2DecreasesUseDurationFurther);
            helper.register(Mod.id("flask_filling_recipe_accepts_flask_and_potion"), flaskAndLauncherGameTests::flaskFillingRecipeAcceptsFlaskAndPotion);
            helper.register(Mod.id("flask_filling_recipe_rejects_over_capacity"), flaskAndLauncherGameTests::flaskFillingRecipeRejectsOverCapacity);
            helper.register(Mod.id("flask_filling_recipe_rejects_mixed_potions"), flaskAndLauncherGameTests::flaskFillingRecipeRejectsMixedPotions);
            helper.register(Mod.id("flask_drinking_applies_instant_healing_effect"), flaskAndLauncherGameTests::flaskDrinkingAppliesInstantHealingEffect);
            helper.register(Mod.id("potion_launcher_filling_recipe_accepts_ender_bow_and_splash_potion"), flaskAndLauncherGameTests::potionLauncherFillingRecipeAcceptsEnderBowAndSplashPotion);
            helper.register(Mod.id("potion_launcher_fires_splash_potion_by_default"), flaskAndLauncherGameTests::potionLauncherFiresSplashPotionByDefault);
            helper.register(Mod.id("potion_launcher_fires_lingering_potion_when_flag_set"), flaskAndLauncherGameTests::potionLauncherFiresLingeringPotionWhenFlagSet);
            helper.register(Mod.id("potion_launcher_fails_with_zero_charges"), flaskAndLauncherGameTests::potionLauncherFailsWithZeroCharges);
            GrabHookGameTests grabHookGameTests = new GrabHookGameTests();
            helper.register(Mod.id("grab_hook_launches_with_default_speed"), grabHookGameTests::grabHookLaunchesWithDefaultSpeed);
            helper.register(Mod.id("grab_hook_turbo_grab_increases_launch_speed"), grabHookGameTests::grabHookTurboGrabIncreasesLaunchSpeed);
            helper.register(Mod.id("grab_hook_pull_amount_scales_speed"), grabHookGameTests::grabHookPullAmountScalesSpeed);
            helper.register(Mod.id("grab_hook_starts_retracting_after_air_time"), grabHookGameTests::grabHookStartsRetractingAfterAirTime);
            helper.register(Mod.id("grab_hook_catches_entity_on_path"), grabHookGameTests::grabHookCatchesEntityOnPath);
            helper.register(Mod.id("grab_hook_damages_entity_by_default"), grabHookGameTests::grabHookDamagesEntityByDefault);
            helper.register(Mod.id("grab_hook_keeps_corpse_when_entity_dies_on_impact"), grabHookGameTests::grabHookKeepsCorpseWhenEntityDiesOnImpact);
            helper.register(Mod.id("grab_hook_gentle_grab_deals_no_damage"), grabHookGameTests::grabHookGentleGrabDealsNoDamage);
            ScytheGameTests scytheGameTests = new ScytheGameTests();
            helper.register(Mod.id("scythe_can_harvest_max_age_crop"), scytheGameTests::scytheCanHarvestMaxAgeCrop);
            helper.register(Mod.id("scythe_cannot_harvest_young_crop"), scytheGameTests::scytheCannotHarvestYoungCrop);
            helper.register(Mod.id("scythe_mine_block_harvests_adjacent_crops_with_sweeping_edge"), scytheGameTests::scytheMineBlockHarvestsAdjacentCropsWithSweepingEdge);
            helper.register(Mod.id("scythe_mine_block_sweeping_level2_harvests_diagonals"), scytheGameTests::scytheMineBlockSweepingLevel2HarvestsDiagonals);
            helper.register(Mod.id("scythe_without_sweeping_edge_does_not_harvest_neighbors"), scytheGameTests::scytheWithoutSweepingEdgeDoesNotHarvestNeighbors);
            helper.register(Mod.id("all_knives_are_in_knife_tag"), scytheGameTests::allKnivesAreInKnifeTag);
            helper.register(Mod.id("guillotine_requires_full_pull_to_activate"), scytheGameTests::guillotineRequiresFullPullToActivate);
            helper.register(Mod.id("guillotine_kill_drops_head"), scytheGameTests::guillotineKillDropsHead);
            WeaponsGameTests weaponsGameTests = new WeaponsGameTests();
            helper.register(Mod.id("warhammer_damages_entity_and_loses_durability"), weaponsGameTests::warhammerDamagesEntityAndLosesDurability);
            helper.register(Mod.id("devour_kill_spawns_health_orbs"), weaponsGameTests::devourKillSpawnsHealthOrbs);
            helper.register(Mod.id("health_orb_heals_player_on_touch"), weaponsGameTests::healthOrbHealsPlayerOnTouch);
            helper.register(Mod.id("warhammer_get_full_use_seconds_base_is_one"), weaponsGameTests::warhammerGetFullUseSecondsBaseIsOne);
            helper.register(Mod.id("warhammer_pull_below_threshold_does_nothing"), weaponsGameTests::warhammerPullBelowThresholdDoesNothing);
            helper.register(Mod.id("warhammer_short_pull_deals_reduced_damage"), weaponsGameTests::warhammerShortPullDealsReducedDamage);
            helper.register(Mod.id("warhammer_medium_pull_deals_more_damage_than_short"), weaponsGameTests::warhammerMediumPullDealsMoreDamageThanShort);
            helper.register(Mod.id("warhammer_full_pull_deals_max_damage"), weaponsGameTests::warhammerFullPullDealsMaxDamage);
            helper.register(Mod.id("warhammer_density_increases_full_use_seconds"), weaponsGameTests::warhammerDensityIncreasesFullUseSeconds);
            helper.register(Mod.id("warhammer_quick_decreases_full_use_seconds"), weaponsGameTests::warhammerQuickDecreasesFullUseSeconds);
            helper.register(Mod.id("warhammer_density_takes_precedence_over_quick"), weaponsGameTests::warhammerDensityTakesPrecedenceOverQuick);
            helper.register(Mod.id("warhammer_wind_burst_fires_wind_charge_on_miss"), weaponsGameTests::warhammerWindBurstFiresWindChargeOnMiss);
            helper.register(Mod.id("warhammer_maim_applies_slowness"), weaponsGameTests::warhammerMaimAppliesSlowness);
            helper.register(Mod.id("warhammer_maim_level2_applies_stronger_slowness"), weaponsGameTests::warhammerMaimLevel2AppliesStrongerSlowness);
            helper.register(Mod.id("warhammer_uppercut_applies_upward_velocity"), weaponsGameTests::warhammerUppercutAppliesUpwardVelocity);
            helper.register(Mod.id("warhammer_quake_hits_nearby_entities_when_ground_hit"), weaponsGameTests::warhammerQuakeHitsNearbyEntitiesWhenGroundHit);
            helper.register(Mod.id("warhammer_quake_does_nothing_without_enchantment"), weaponsGameTests::warhammerQuakeDoesNothingWithoutEnchantment);
            helper.register(Mod.id("warhammer_dash_applies_forward_velocity"), weaponsGameTests::warhammerDashAppliesForwardVelocity);
            helper.register(Mod.id("warhammer_dash_strikes_entity_in_range"), weaponsGameTests::warhammerDashStrikesEntityInRange);
            helper.register(Mod.id("warhammer_dash_already_hit_does_not_strike_again"), weaponsGameTests::warhammerDashAlreadyHitDoesNotStrikeAgain);
            helper.register(Mod.id("warhammer_allows_mod_specific_enchantments"), weaponsGameTests::warhammerAllowsModSpecificEnchantments);
            helper.register(Mod.id("warhammer_allows_mace_enchantments"), weaponsGameTests::warhammerAllowsMaceEnchantments);
            helper.register(Mod.id("density_is_exclusive_with_breach_and_quick"), weaponsGameTests::densityIsExclusiveWithBreachAndQuick);
            helper.register(Mod.id("warhammer_allows_vanilla_whitelisted_enchantments"), weaponsGameTests::warhammerAllowsVanillaWhitelistedEnchantments);
            helper.register(Mod.id("warhammer_blocks_off_limit_enchantments"), weaponsGameTests::warhammerBlocksOffLimitEnchantments);
            helper.register(Mod.id("knife_backstab_bonus_when_sneak"), weaponsGameTests::knifeBackstabBonusWhenSneak);
            helper.register(Mod.id("knife_no_backstab_bonus_from_front"), weaponsGameTests::knifeNoBackstabBonusFromFront);
            helper.register(Mod.id("knife_no_backstab_bonus_when_not_sneaking"), weaponsGameTests::knifeNoBackstabBonusWhenNotSneaking);
            helper.register(Mod.id("knife_stealth_enchantment_increases_backstab_bonus"), weaponsGameTests::knifeStealthEnchantmentIncreasesBackstabBonus);
            helper.register(Mod.id("knife_stealth_level2_increases_backstab_further"), weaponsGameTests::knifeStealthLevel2IncreasesBackstabFurther);
            helper.register(Mod.id("knife_offhand_use_adds_cooldown"), weaponsGameTests::knifeOffhandUseAddsCooldown);
            helper.register(Mod.id("knife_poison_enchantment_applies_effect_on_hit"), weaponsGameTests::knifePoisonEnchantmentAppliesEffectOnHit);
            helper.register(Mod.id("knife_poison_level2_has_longer_duration"), weaponsGameTests::knifePoisonLevel2HasLongerDuration);
            helper.register(Mod.id("knife_stealth_hit_deals_full_bonus_despite_hurt_resistance"), weaponsGameTests::knifeStealthHitDealsFullBonusDespiteHurtResistance);
            helper.register(Mod.id("knife_offhand_damage_matches_main_hand_for_every_tier"), weaponsGameTests::knifeOffhandDamageMatchesMainHandForEveryTier);
            helper.register(Mod.id("knife_offhand_interaction_deals_main_hand_damage"), weaponsGameTests::knifeOffhandInteractionDealsMainHandDamage);
            helper.register(Mod.id("knife_offhand_damage_base_is_three"), weaponsGameTests::knifeOffhandDamageBaseIsThree);
            helper.register(Mod.id("knife_offhand_damage_scales_with_sharpness"), weaponsGameTests::knifeOffhandDamageScalesWithSharpness);
        });
    }
}
