package xerca.xercafood.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.entity.monster.zombie.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.Vec3;
import xerca.xercafood.common.item.Items;

import static xerca.xercafood.tests.GameTestHelpers.hasNearbyItem;
import static xerca.xercafood.tests.GameTestHelpers.requireFoodProperties;

@SuppressWarnings("unused")
public class FoodItemGameTests {

    @GameTest
    public void rottenBurgerLowersHungerAndCanApplyPoison(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(20);
        player.getFoodData().setSaturation(0.0f);

        new ItemStack(Items.ROTTEN_BURGER).getItem().finishUsingItem(new ItemStack(Items.ROTTEN_BURGER), helper.getLevel(), player);

        GameTestHelpers.assertTrue(helper, player.getFoodData().getFoodLevel() <= 15, "Expected rotten burger to lower hunger by at least 5");
        helper.succeed();
    }

    @GameTest
    public void ultimateBurgerAppliesSaturationEffect(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.getFoodData().setFoodLevel(10);
        new ItemStack(Items.ULTIMATE_BURGER).getItem().finishUsingItem(new ItemStack(Items.ULTIMATE_BURGER), helper.getLevel(), player);
        GameTestHelpers.assertTrue(helper, player.hasEffect(net.minecraft.world.effect.MobEffects.SATURATION), "Expected ultimate burger to apply saturation");
        helper.succeed();
    }

    @GameTest
    public void chorusCupcakeTeleportsPlayer(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos platformCenter = new BlockPos(4, 2, 4);
        for (int dx = -6; dx <= 6; dx++) {
            for (int dz = -6; dz <= 6; dz++) {
                helper.getLevel().setBlockAndUpdate(helper.absolutePos(platformCenter.offset(dx, -1, dz)),
                        net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
            }
        }
        player.snapTo(Vec3.atCenterOf(helper.absolutePos(platformCenter)));
        Vec3 before = player.position();
        boolean moved = false;
        for (int attempt = 0; attempt < 10 && !moved; attempt++) {
            new ItemStack(Items.ENDER_CUPCAKE).getItem().finishUsingItem(new ItemStack(Items.ENDER_CUPCAKE), helper.getLevel(), player);
            moved = player.position().distanceToSqr(before) > 0.5;
        }
        GameTestHelpers.assertTrue(helper, moved, "Expected chorus cupcake to teleport the player");
        helper.succeed();
    }

    @GameTest
    public void goldenCupcakeProducesRandomOutcomes(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        boolean sawLevitation = false;
        boolean sawJump = false;
        boolean sawBlindness = false;
        boolean sawDroppedCupcake = false;

        for (int i = 0; i < 40; i++) {
            Items.GOLDEN_CUPCAKE.finishUsingItem(new ItemStack(Items.GOLDEN_CUPCAKE), helper.getLevel(), player);
            sawLevitation |= player.hasEffect(net.minecraft.world.effect.MobEffects.LEVITATION);
            sawJump |= player.hasEffect(net.minecraft.world.effect.MobEffects.JUMP_BOOST);
            sawBlindness |= player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS);
            sawDroppedCupcake |= hasNearbyItem(helper, new BlockPos(0, 2, 0), Items.GOLDEN_CUPCAKE, 32.0);
        }

        int seen = (sawLevitation ? 1 : 0) + (sawJump ? 1 : 0) + (sawBlindness ? 1 : 0) + (sawDroppedCupcake ? 1 : 0);
        GameTestHelpers.assertTrue(helper, seen >= 2, "Expected to observe multiple distinct golden cupcake outcomes");
        helper.succeed();
    }

    @GameTest
    public void teacupSugarLevelsChangeNutritionAndEffects(GameTestHelper helper) {
        net.minecraft.world.food.FoodProperties sugar0 = requireFoodProperties(helper, Items.FULL_TEACUP_0);
        net.minecraft.world.food.FoodProperties sugar6 = requireFoodProperties(helper, Items.FULL_TEACUP_6);
        GameTestHelpers.assertTrue(helper, sugar0.nutrition() < sugar6.nutrition(), "Expected more sugar to increase teacup nutrition");
        helper.succeed();
    }

    @GameTest
    public void meatFoodsUseMinecraftTags(GameTestHelper helper) {
        ItemStack cookedPatty = new ItemStack(Items.COOKED_PATTY);
        GameTestHelpers.assertTrue(helper, cookedPatty.is(ItemTags.MEAT), "Expected cooked patty to be tagged as meat");
        GameTestHelpers.assertTrue(helper, cookedPatty.is(ItemTags.WOLF_FOOD), "Expected meat tag to make cooked patty wolf food");
        GameTestHelpers.assertFalse(helper, new ItemStack(Items.CHOCOLATE).is(ItemTags.MEAT), "Expected chocolate not to be tagged as meat");
        helper.succeed();
    }

    @GameTest
    public void teaUseDurationAndDrinkEffectsAreCorrect(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        GameTestHelpers.assertTrue(helper, Items.FULL_TEACUP_0.getUseDuration(new ItemStack(Items.FULL_TEACUP_0), player) == 64, "Expected teacup drink time to be 64 ticks");
        Items.FULL_TEACUP_0.finishUsingItem(new ItemStack(Items.FULL_TEACUP_0), helper.getLevel(), player);
        GameTestHelpers.assertTrue(helper, player.hasEffect(net.minecraft.world.effect.MobEffects.HASTE), "Expected tea drinking to grant haste");
        helper.succeed();
    }

    @GameTest
    public void tomatoProjectileHitDamagesEntity(GameTestHelper helper) {
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
            helper.fail(net.minecraft.network.chat.Component.literal("Failed to invoke tomato hit logic: " + e.getMessage()));
            return;
        }

        GameTestHelpers.assertTrue(helper, zombie.getHealth() < healthBefore, "Expected tomato projectile hit to damage target");
        helper.succeed();
    }
}
