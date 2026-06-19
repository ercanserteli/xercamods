package xerca.xercafood.tests;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.EntityHitResult;
import xerca.xercafood.common.item.Items;

import static xerca.xercafood.tests.GameTestHelpers.*;

public class AdvancementGameTests {

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teaAdvancementOnlyTriggersForHotTeapots(GameTestHelper helper) {
        ServerPlayer player = makeServerPlayer(helper);
        AdvancementHolder advancement = requireAdvancement(helper, advancementId("achievements/brew_tea"));

        helper.assertFalse(advancementProgress(player, advancement).isDone(), "Expected brew tea advancement to start locked");

        triggerInventoryChanged(player, new ItemStack(Items.TOMATO));
        helper.assertFalse(advancementProgress(player, advancement).isDone(), "Tomato should not trigger the brew tea advancement");

        triggerInventoryChanged(player, new ItemStack(Items.HOT_TEAPOT_0));
        helper.assertTrue(advancementProgress(player, advancement).isDone(), "Hot teapot should trigger the brew tea advancement");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void recipeAdvancementUnlocksTomatoSlicingRecipe(GameTestHelper helper) {
        ServerPlayer player = makeServerPlayer(helper);
        net.minecraft.resources.ResourceKey<net.minecraft.world.item.crafting.Recipe<?>> tomatoSlicesRecipeId =
                net.minecraft.resources.ResourceKey.create(net.minecraft.core.registries.Registries.RECIPE, recipeId("tomato_slices"));
        AdvancementHolder advancement = requireAdvancement(helper, advancementId("recipes/tomato_slices"));

        helper.assertFalse(advancementProgress(player, advancement).isDone(), "Expected tomato slices recipe advancement to start locked");
        helper.assertFalse(player.getRecipeBook().contains(tomatoSlicesRecipeId), "Expected tomato slices recipe to start locked in the recipe book");

        triggerInventoryChanged(player, new ItemStack(Items.TOMATO));

        helper.assertTrue(advancementProgress(player, advancement).isDone(), "Expected tomato item pickup to complete the tomato slices recipe advancement");
        helper.assertTrue(player.getRecipeBook().contains(tomatoSlicesRecipeId), "Expected tomato item pickup to unlock the tomato slices recipe");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void advancementTriggersForUltimateBurgerTomatoHitAndGoldenCupcake(GameTestHelper helper) {
        ServerPlayer player = makeServerPlayer(helper);
        AdvancementHolder ultimateBurgerAdv = requireAdvancement(helper, advancementId("achievements/fat_fuck"));
        AdvancementHolder tomatoShotAdv = requireAdvancement(helper, advancementId("achievements/shoot_tomato"));
        AdvancementHolder cupcakeAdv = requireAdvancement(helper, advancementId("achievements/cupcake"));

        helper.assertFalse(advancementProgress(player, ultimateBurgerAdv).isDone(), "Expected ultimate burger advancement to start locked");
        helper.assertFalse(advancementProgress(player, tomatoShotAdv).isDone(), "Expected tomato-hit advancement to start locked");
        helper.assertFalse(advancementProgress(player, cupcakeAdv).isDone(), "Expected cupcake possession advancement to start locked");

        Items.ULTIMATE_BURGER.finishUsingItem(new ItemStack(Items.ULTIMATE_BURGER), helper.getLevel(), player);
        helper.assertTrue(advancementProgress(player, ultimateBurgerAdv).isDone(), "Expected ultimate burger consumption to unlock advancement");

        triggerInventoryChanged(player, new ItemStack(Items.GOLDEN_CUPCAKE));
        helper.assertTrue(advancementProgress(player, cupcakeAdv).isDone(), "Expected golden cupcake possession to unlock advancement");

        Zombie zombie = new Zombie(helper.getLevel());
        zombie.setPos(player.getX() + 1, player.getY(), player.getZ());
        helper.getLevel().addFreshEntity(zombie);
        xerca.xercafood.common.entity.EntityTomato tomato = new xerca.xercafood.common.entity.EntityTomato(helper.getLevel(), player);
        try {
            java.lang.reflect.Method onHit = xerca.xercafood.common.entity.EntityTomato.class.getDeclaredMethod("onHit", net.minecraft.world.phys.HitResult.class);
            onHit.setAccessible(true);
            onHit.invoke(tomato, new EntityHitResult(zombie));
        } catch (Exception e) {
            helper.fail("Failed to invoke tomato hit logic: " + e.getMessage());
            return;
        }
        helper.assertTrue(advancementProgress(player, tomatoShotAdv).isDone(), "Expected tomato projectile hit to unlock tomato-hit advancement");
        helper.succeed();
    }
}
