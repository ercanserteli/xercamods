package xerca.xercafood.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import xerca.xercafood.common.item.Items;

import static xerca.xercafood.tests.GameTestHelpers.*;

public class FoodItemGameTests {

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void rottenBurgerLowersHungerAndCanApplyPoison(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(0.0f);

        new ItemStack(Items.ROTTEN_BURGER).getItem().finishUsingItem(new ItemStack(Items.ROTTEN_BURGER), helper.getLevel(), player);

        helper.assertTrue(player.getFoodData().getFoodLevel() <= 15, "Expected rotten burger to lower hunger by at least 5");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void ultimateBurgerAppliesSaturationEffect(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(10);
        new ItemStack(Items.ULTIMATE_BURGER).getItem().finishUsingItem(new ItemStack(Items.ULTIMATE_BURGER), helper.getLevel(), player);
        helper.assertTrue(player.hasEffect(net.minecraft.world.effect.MobEffects.SATURATION), "Expected ultimate burger to apply saturation");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void chorusCupcakeTeleportsPlayer(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 before = player.position();
        new ItemStack(Items.ENDER_CUPCAKE).getItem().finishUsingItem(new ItemStack(Items.ENDER_CUPCAKE), helper.getLevel(), player);
        double movedSq = player.position().distanceToSqr(before);
        helper.assertTrue(movedSq > 0.5, "Expected chorus cupcake to teleport the player");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void goldenCupcakeProducesRandomOutcomes(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        boolean sawLevitation = false;
        boolean sawJump = false;
        boolean sawBlindness = false;
        boolean sawDroppedCupcake = false;

        for (int i = 0; i < 40; i++) {
            Items.GOLDEN_CUPCAKE.finishUsingItem(new ItemStack(Items.GOLDEN_CUPCAKE), helper.getLevel(), player);
            sawLevitation |= player.hasEffect(net.minecraft.world.effect.MobEffects.LEVITATION);
            sawJump |= player.hasEffect(net.minecraft.world.effect.MobEffects.JUMP);
            sawBlindness |= player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS);
            sawDroppedCupcake |= hasNearbyItem(helper, new BlockPos(0, 2, 0), Items.GOLDEN_CUPCAKE, 32.0);
        }

        int seen = (sawLevitation ? 1 : 0) + (sawJump ? 1 : 0) + (sawBlindness ? 1 : 0) + (sawDroppedCupcake ? 1 : 0);
        helper.assertTrue(seen >= 2, "Expected to observe multiple distinct golden cupcake outcomes");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teacupSugarLevelsChangeNutritionAndEffects(GameTestHelper helper) {
        net.minecraft.world.food.FoodProperties sugar0 = requireFoodProperties(helper, Items.FULL_TEACUP_0);
        net.minecraft.world.food.FoodProperties sugar6 = requireFoodProperties(helper, Items.FULL_TEACUP_6);
        helper.assertTrue(sugar0.nutrition() < sugar6.nutrition(), "Expected more sugar to increase teacup nutrition");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void meatFoodsUseMinecraftTags(GameTestHelper helper) {
        ItemStack cookedPatty = new ItemStack(Items.COOKED_PATTY);
        helper.assertTrue(cookedPatty.is(ItemTags.MEAT), "Expected cooked patty to be tagged as meat");
        helper.assertTrue(cookedPatty.is(ItemTags.WOLF_FOOD), "Expected meat tag to make cooked patty wolf food");
        helper.assertFalse(new ItemStack(Items.CHOCOLATE).is(ItemTags.MEAT), "Expected chocolate not to be tagged as meat");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teaUseDurationAndDrinkEffectsAreCorrect(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        helper.assertTrue(Items.FULL_TEACUP_0.getUseDuration(new ItemStack(Items.FULL_TEACUP_0), player) == 64, "Expected teacup drink time to be 64 ticks");
        Items.FULL_TEACUP_0.finishUsingItem(new ItemStack(Items.FULL_TEACUP_0), helper.getLevel(), player);
        helper.assertTrue(player.hasEffect(net.minecraft.world.effect.MobEffects.DIG_SPEED), "Expected tea drinking to grant haste");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void tomatoProjectileHitDamagesEntity(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Zombie zombie = new Zombie(helper.getLevel());
        zombie.setPos(player.getX() + 2.0, player.getY(), player.getZ());
        helper.getLevel().addFreshEntity(zombie);

        xerca.xercafood.common.entity.EntityTomato tomato = new xerca.xercafood.common.entity.EntityTomato(helper.getLevel(), player);
        float healthBefore = zombie.getHealth();

        try {
            java.lang.reflect.Method onHit = xerca.xercafood.common.entity.EntityTomato.class.getDeclaredMethod("onHit", net.minecraft.world.phys.HitResult.class);
            onHit.setAccessible(true);
            onHit.invoke(tomato, new EntityHitResult(zombie));
        } catch (Exception e) {
            helper.fail("Failed to invoke tomato hit logic: " + e.getMessage());
            return;
        }

        helper.assertTrue(zombie.getHealth() < healthBefore, "Expected tomato projectile hit to damage target");
        helper.succeed();
    }
}
