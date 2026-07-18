package xerca.xercafood.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import xerca.xercafood.common.item.Items;

import java.util.Optional;

import static xerca.xercafood.tests.GameTestHelpers.*;

public class RecipeGameTests {

    @GameTest
    public void shapedRecipeCraftsAppleCupcake(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("apple_cupcake"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.APPLE), new ItemStack(net.minecraft.world.item.Items.SUGAR),
                new ItemStack(net.minecraft.world.item.Items.MILK_BUCKET), new ItemStack(net.minecraft.world.item.Items.EGG), new ItemStack(net.minecraft.world.item.Items.MILK_BUCKET),
                new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT)
        );

        assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected apple cupcake recipe to match");
        ItemStack result = recipe.assemble(grid);
        assertTrue(helper, result.is(Items.APPLE_CUPCAKE), "Expected apple cupcake result item");
        assertTrue(helper, result.getCount() == 6, "Expected apple cupcake recipe to craft 6 items");
        helper.succeed();
    }

    @GameTest
    public void shapelessRecipeCraftsColaPowder(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("cola_powder"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(net.minecraft.world.item.Items.WARPED_ROOTS), new ItemStack(net.minecraft.world.item.Items.ROTTEN_FLESH), new ItemStack(net.minecraft.world.item.Items.SPIDER_EYE),
                new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.SUGAR),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected cola powder recipe to match");
        ItemStack result = recipe.assemble(grid);
        assertTrue(helper, result.is(Items.COLA_POWDER), "Expected cola powder result item");
        helper.succeed();
    }

    @GameTest
    public void slicingTomatoDamagesKnife(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("tomato_slices"));
        net.minecraft.world.item.Item knifeItem = requireKnifeItem();
        ItemStack knife = new ItemStack(knifeItem);
        knife.setDamageValue(7);
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(Items.TOMATO), knife,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected tomato slicing recipe to match");
        ItemStack result = recipe.assemble(grid);
        assertTrue(helper, result.is(Items.TOMATO_SLICES), "Expected tomato slicing to produce tomato slices");
        assertTrue(helper, result.getCount() == 3, "Expected tomato slicing recipe to craft 3 slices");

        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(grid);
        assertTrue(helper, remainingItems.get(1).is(knifeItem), "Expected knife to remain after slicing tomato");
        assertTrue(helper, remainingItems.get(1).getDamageValue() == 8, "Expected knife durability to decrease by 1");
        helper.succeed();
    }

    @GameTest
    public void repairingKnivesHasNoCraftingRecipe(GameTestHelper helper) {
        net.minecraft.world.item.Item knifeItem = requireKnifeItem();
        ItemStack firstKnife = new ItemStack(knifeItem);
        ItemStack secondKnife = new ItemStack(knifeItem);
        firstKnife.setDamageValue(30);
        secondKnife.setDamageValue(70);

        CraftingInput grid = craftingGrid(2, 1, firstKnife, secondKnife);
        Optional<RecipeHolder<CraftingRecipe>> recipe = helper.getLevel()
                .recipeAccess()
                .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());

        assertTrue(helper, recipe.isEmpty(), "Expected damaged knives to have no crafting repair recipe");
        helper.succeed();
    }

    @GameTest
    public void smeltingRecipeCooksRawPatty(GameTestHelper helper) {
        SmeltingRecipe recipe = requireSmeltingRecipe(helper, recipeId("smelting_cooked_patty"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.RAW_PATTY));

        assertTrue(helper, recipe.matches(input, helper.getLevel()), "Expected smelting recipe to match raw patty input");
        ItemStack result = recipe.assemble(input);
        assertTrue(helper, result.is(Items.COOKED_PATTY), "Expected smelting recipe to produce cooked patty");
        helper.succeed();
    }

    @GameTest
    public void smokingRecipeCooksRawSausage(GameTestHelper helper) {
        SmokingRecipe recipe = requireSmokingRecipe(helper, recipeId("smoking_cooked_sausage"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.RAW_SAUSAGE));

        assertTrue(helper, recipe.matches(input, helper.getLevel()), "Expected smoking recipe to match raw sausage input");
        ItemStack result = recipe.assemble(input);
        assertTrue(helper, result.is(Items.COOKED_SAUSAGE), "Expected smoking recipe to produce cooked sausage");
        helper.succeed();
    }

    @GameTest
    public void campfireRecipeLoadsAndCooksPizzaVariant(GameTestHelper helper) {
        CampfireCookingRecipe recipe = requireCampfireRecipe(helper, recipeId("campfire_cooking_pizza_chicken_mushroom_mushroom"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.RAW_PIZZA_CHICKEN_MUSHROOM_MUSHROOM));

        assertTrue(helper, recipe.matches(input, helper.getLevel()), "Expected campfire pizza recipe to match raw pizza input");
        ItemStack result = recipe.assemble(input);
        assertTrue(helper, result.is(Items.PIZZA_CHICKEN_MUSHROOM_MUSHROOM), "Expected cooked pizza variant result item");
        helper.succeed();
    }

    @GameTest(maxTicks = 500)
    public void pizzaRecipeWorksEndToEnd(GameTestHelper helper) {
        CraftingRecipe pizzaBaseRecipe = requireCraftingRecipe(helper, recipeId("raw_pizza"));
        CraftingInput pizzaBaseGrid = craftingGrid(3, 2,
                new ItemStack(Items.CHEESE_SLICE), new ItemStack(Items.TOMATO_SLICES), ItemStack.EMPTY,
                new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT)
        );

        assertTrue(helper, pizzaBaseRecipe.matches(pizzaBaseGrid, helper.getLevel()), "Expected raw pizza base recipe to match");
        ItemStack rawPizza = pizzaBaseRecipe.assemble(pizzaBaseGrid);
        assertTrue(helper, rawPizza.is(Items.RAW_PIZZA), "Expected raw pizza base recipe to produce raw pizza");

        CraftingRecipe toppingRecipe = requireCraftingRecipe(helper, recipeId("raw_pizza_chicken_mushroom"));
        CraftingInput toppingGrid = craftingGrid(3, 3,
                rawPizza.copy(), new ItemStack(net.minecraft.world.item.Items.CHICKEN), new ItemStack(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        assertTrue(helper, toppingRecipe.matches(toppingGrid, helper.getLevel()), "Expected pizza topping recipe to match");
        ItemStack toppedPizza = toppingRecipe.assemble(toppingGrid);
        assertTrue(helper, toppedPizza.is(Items.RAW_PIZZA_CHICKEN_MUSHROOM), "Expected topped pizza recipe to produce raw chicken mushroom pizza");

        CampfireCookingRecipe cookingRecipe = requireCampfireRecipe(helper, recipeId("campfire_cooking_pizza_chicken_mushroom"));
        SingleRecipeInput cookingInput = new SingleRecipeInput(toppedPizza.copy());
        assertTrue(helper, cookingRecipe.matches(cookingInput, helper.getLevel()), "Expected campfire recipe to match topped raw pizza");
        ItemStack cookedPizza = cookingRecipe.assemble(cookingInput);
        assertTrue(helper, cookedPizza.is(Items.PIZZA_CHICKEN_MUSHROOM), "Expected end-to-end pizza flow to produce cooked chicken mushroom pizza");
        helper.succeed();
    }

    @GameTest
    public void teaPouringFillsCupsAndReturnsReducedTeapot(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("tea_pouring"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(Items.HOT_TEAPOT_3), new ItemStack(Items.TEACUP), new ItemStack(Items.TEACUP),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected tea pouring recipe to match teapot plus cups");
        ItemStack result = recipe.assemble(grid);
        assertTrue(helper, result.is(Items.FULL_TEACUP_0), "Expected tea pouring to produce filled teacups");
        assertTrue(helper, result.getCount() == 2, "Expected tea pouring to fill two teacups");

        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(grid);
        assertTrue(helper, remainingItems.get(0).is(Items.HOT_TEAPOT_1), "Expected pouring two cups from a 3-cup teapot to leave a 1-cup hot teapot");
        helper.succeed();
    }

    @GameTest
    public void teaSugaringIncreasesSugarLevel(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("tea_sugaring"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(Items.FULL_TEACUP_0), new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.SUGAR),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected tea sugaring recipe to match teacup plus sugar");
        ItemStack result = recipe.assemble(grid);
        assertTrue(helper, result.is(Items.FULL_TEACUP_2), "Expected two sugars to produce the two-sugar teacup");
        helper.succeed();
    }

    @GameTest(maxTicks = 500)
    public void brewingProducesTexturedColaExtractAndCraftsCola(GameTestHelper helper) {
        BlockPos brewingStandPos = new BlockPos(1, 1, 1);
        helper.setBlock(brewingStandPos, Blocks.BREWING_STAND);

        BrewingStandBlockEntity stand = helper.getBlockEntity(brewingStandPos, BrewingStandBlockEntity.class);
        stand.setItem(0, PotionContents.createItemStack(net.minecraft.world.item.Items.POTION, Potions.WATER));
        stand.setItem(3, new ItemStack(Items.COLA_POWDER));
        stand.setItem(4, new ItemStack(net.minecraft.world.item.Items.BLAZE_POWDER));
        stand.setChanged();

        helper.runAtTickTime(420, () -> {
            BrewingStandBlockEntity brewedStand = helper.getBlockEntity(brewingStandPos, BrewingStandBlockEntity.class);
            ItemStack brewedExtract = brewedStand.getItem(0).copy();

            assertTrue(helper, brewedExtract.is(Items.COLA_EXTRACT), "Expected brewing stand output to be the cola extract item");

            CraftingRecipe colaRecipe = requireCraftingRecipe(helper, recipeId("cola"));
            CraftingInput colaGrid = craftingGrid(3, 3,
                    new ItemStack(net.minecraft.world.item.Items.SNOWBALL), brewedExtract.copy(), new ItemStack(net.minecraft.world.item.Items.SNOWBALL),
                    new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER),
                    new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER)
            );

            assertTrue(helper, colaRecipe.matches(colaGrid, helper.getLevel()), "Expected cola recipe to accept brewed cola extract");
            ItemStack colaResult = colaRecipe.assemble(colaGrid);
            assertTrue(helper, colaResult.is(Items.COLA), "Expected cola recipe to craft cola");
            assertTrue(helper, colaResult.getCount() == 6, "Expected cola recipe to craft 6 cola items");

            NonNullList<ItemStack> remainingItems = colaRecipe.getRemainingItems(colaGrid);
            assertTrue(helper, remainingItems.get(1).is(net.minecraft.world.item.Items.GLASS_BOTTLE),
                    "Expected brewed cola extract input to leave behind a glass bottle");

            helper.succeed();
        });
    }

    @GameTest
    public void teaLeafSmeltsToDriedTeaLeaves(GameTestHelper helper) {
        SmeltingRecipe recipe = requireSmeltingRecipe(helper, recipeId("smelting_tea_dried"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.TEA_LEAF));
        assertTrue(helper, recipe.matches(input, helper.getLevel()), "Expected tea leaf smelting recipe to match");
        assertTrue(helper, recipe.assemble(input).is(Items.TEA_DRIED), "Expected tea leaf smelting result to be dried tea");
        helper.succeed();
    }

    @GameTest
    public void teaFillingAndRefillingEdgeCasesWork(GameTestHelper helper) {
        CraftingRecipe filling = requireCraftingRecipe(helper, recipeId("tea_filling"));
        CraftingInput fillToFull = craftingGrid(3, 3,
                new ItemStack(Items.TEAPOT), new ItemStack(net.minecraft.world.item.Items.WATER_BUCKET), new ItemStack(Items.TEA_DRIED),
                new ItemStack(Items.TEA_DRIED), new ItemStack(Items.TEA_DRIED), new ItemStack(Items.TEA_DRIED),
                new ItemStack(Items.TEA_DRIED), new ItemStack(Items.TEA_DRIED), new ItemStack(Items.TEA_DRIED)
        );
        assertTrue(helper, filling.matches(fillToFull, helper.getLevel()), "Expected full tea fill path to match");
        assertTrue(helper, filling.assemble(fillToFull).is(Items.FULL_TEAPOT_7), "Expected full tea fill path to produce 7-tea teapot");

        CraftingRecipe refilling = requireCraftingRecipe(helper, recipeId("tea_refilling"));
        CraftingInput tooManyLeaves = craftingGrid(3, 3,
                new ItemStack(Items.FULL_TEAPOT_6), new ItemStack(Items.TEA_DRIED), new ItemStack(Items.TEA_DRIED),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );
        assertFalse(helper, refilling.matches(tooManyLeaves, helper.getLevel()), "Expected refilling above max to fail");

        CraftingInput hotTeapotRefill = craftingGrid(3, 3,
                new ItemStack(Items.HOT_TEAPOT_3), new ItemStack(Items.TEA_DRIED), ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );
        assertFalse(helper, refilling.matches(hotTeapotRefill, helper.getLevel()), "Expected hot teapot refilling to fail");
        helper.succeed();
    }

    @GameTest
    public void pouringLastCupRevertsHotTeapotToEmptyTeapot(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("tea_pouring"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(Items.HOT_TEAPOT_1), new ItemStack(Items.TEACUP), ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );
        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(grid);
        assertTrue(helper, remainingItems.get(0).is(Items.TEAPOT), "Expected empty teapot after pouring the final cup");
        helper.succeed();
    }

    @GameTest
    public void sparklingWaterAndSodaColaFlowsWork(GameTestHelper helper) {
        BlockPos soulSandPos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(soulSandPos, net.minecraft.world.level.block.Blocks.SOUL_SAND.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(soulSandPos.above(), net.minecraft.world.level.block.Blocks.BUBBLE_COLUMN.defaultBlockState());
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack glass = new ItemStack(Items.GLASS);
        player.getInventory().setSelectedItem(glass);

        Items.GLASS.useOn(new net.minecraft.world.item.context.UseOnContext(player, InteractionHand.MAIN_HAND, hitTopOf(soulSandPos)));
        assertTrue(helper, player.getInventory().contains(new ItemStack(Items.CARBONATED_WATER)), "Expected bubble column interaction to create carbonated water");

        CraftingRecipe sodaRecipe = requireCraftingRecipe(helper, recipeId("soda"));
        CraftingInput sodaGrid = craftingGrid(3, 3,
                new ItemStack(Items.CARBONATED_WATER), new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.SNOWBALL),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );
        assertTrue(helper, sodaRecipe.matches(sodaGrid, helper.getLevel()), "Expected soda recipe to match");
        assertTrue(helper, sodaRecipe.assemble(sodaGrid).is(Items.SODA), "Expected soda recipe output");

        CraftingRecipe colaRecipe = requireCraftingRecipe(helper, recipeId("cola"));
        CraftingInput colaGrid = craftingGrid(3, 3,
                new ItemStack(net.minecraft.world.item.Items.SNOWBALL), new ItemStack(Items.COLA_EXTRACT), new ItemStack(net.minecraft.world.item.Items.SNOWBALL),
                new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER),
                new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER)
        );
        assertTrue(helper, colaRecipe.matches(colaGrid, helper.getLevel()), "Expected cola recipe to match");
        assertTrue(helper, colaRecipe.assemble(colaGrid).is(Items.COLA), "Expected cola recipe output");
        helper.succeed();
    }
}
