package xerca.xercafood.tests;

import net.minecraft.advancements.AdvancementHolder;
import net.minecraft.advancements.AdvancementProgress;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.CampfireCookingRecipe;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SmeltingRecipe;
import net.minecraft.world.item.crafting.SmokingRecipe;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BrewingStandBlockEntity;
import xerca.xercafood.common.Mod;
import xerca.xercafood.common.item.Items;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class RecipeGameTests {
    private static final String BASIC_TEMPLATE = "xercafood:basic_test";
    private static final String RECIPE_BATCH = "xercafood_recipes";

    private static ResourceLocation recipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mod.MODID, path);
    }

    private static ResourceLocation advancementId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mod.MODID, path);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        return (CraftingRecipe) recipe;
    }

    private static CampfireCookingRecipe requireCampfireRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CampfireCookingRecipe, "Expected campfire cooking recipe for " + recipeId);
        return (CampfireCookingRecipe) recipe;
    }

    private static SmeltingRecipe requireSmeltingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof SmeltingRecipe, "Expected smelting recipe for " + recipeId);
        return (SmeltingRecipe) recipe;
    }

    private static SmokingRecipe requireSmokingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof SmokingRecipe, "Expected smoking recipe for " + recipeId);
        return (SmokingRecipe) recipe;
    }

    private static AdvancementHolder requireAdvancement(GameTestHelper helper, ResourceLocation advancementId) {
        AdvancementHolder advancement = helper.getLevel().getServer().getAdvancements().get(advancementId);
        helper.assertTrue(advancement != null, "Missing advancement: " + advancementId);
        return advancement;
    }

    private static AdvancementProgress advancementProgress(ServerPlayer player, AdvancementHolder advancement) {
        return player.getAdvancements().getOrStartProgress(advancement);
    }

    private static void triggerInventoryChanged(ServerPlayer player, ItemStack stack) {
        player.getInventory().add(stack.copy());
        CriteriaTriggers.INVENTORY_CHANGED.trigger(player, player.getInventory(), stack.copy());
    }

    private static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        for (ItemStack stack : stacks) {
            list.add(stack);
        }
        return CraftingInput.of(width, height, list);
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void tomatoPlantDropsTomatoWhenGrown(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_tomato_plant"));
        net.minecraft.world.level.block.state.BlockState grownTomato = block.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3, 3);
        
        helper.setBlock(pos, grownTomato);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
        
        helper.assertItemEntityPresent(Items.ITEM_TOMATO, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void ricePlantDropsRiceSeedsWhenGrown(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_rice_plant"));
        net.minecraft.world.level.block.state.BlockState grownRice = block.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_7, 7);
        
        helper.setBlock(pos, grownRice);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
        
        helper.assertItemEntityPresent(Items.ITEM_RICE_SEEDS, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teaPlantDropsTeaLeafWhenGrown(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_tea_plant"));
        net.minecraft.world.level.block.state.BlockState grownTea = block.defaultBlockState().setValue(net.minecraft.world.level.block.state.properties.BlockStateProperties.AGE_3, 3);
        
        helper.setBlock(pos, grownTea);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
        
        helper.assertItemEntityPresent(Items.ITEM_TEA_LEAF, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void vatDropsVat(GameTestHelper helper) {
        BlockPos pos = new BlockPos(1, 1, 1);
        net.minecraft.world.level.block.Block block = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "vat"));
        net.minecraft.world.level.block.state.BlockState vat = block.defaultBlockState();
        
        helper.setBlock(pos, vat);
        helper.getLevel().destroyBlock(helper.absolutePos(pos), true);
        
        helper.assertItemEntityPresent(Items.VAT, pos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void shapedRecipeCraftsAppleCupcake(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("item_apple_cupcake"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.APPLE), new ItemStack(net.minecraft.world.item.Items.SUGAR),
                new ItemStack(net.minecraft.world.item.Items.MILK_BUCKET), new ItemStack(net.minecraft.world.item.Items.EGG), new ItemStack(net.minecraft.world.item.Items.MILK_BUCKET),
                new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected apple cupcake recipe to match");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_APPLE_CUPCAKE), "Expected apple cupcake result item");
        helper.assertTrue(result.getCount() == 6, "Expected apple cupcake recipe to craft 6 items");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void shapelessRecipeCraftsColaPowder(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("cola_powder"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(net.minecraft.world.item.Items.WARPED_ROOTS), new ItemStack(net.minecraft.world.item.Items.ROTTEN_FLESH), new ItemStack(net.minecraft.world.item.Items.SPIDER_EYE),
                new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.SUGAR),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected cola powder recipe to match");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.COLA_POWDER), "Expected cola powder result item");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void slicingTomatoDamagesKnife(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("item_tomato_slices"));
        ItemStack knife = new ItemStack(Items.ITEM_KNIFE);
        knife.setDamageValue(7);
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(Items.ITEM_TOMATO), knife,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected tomato slicing recipe to match");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_TOMATO_SLICES), "Expected tomato slicing to produce tomato slices");
        helper.assertTrue(result.getCount() == 3, "Expected tomato slicing recipe to craft 3 slices");

        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(grid);
        helper.assertTrue(remainingItems.get(1).is(Items.ITEM_KNIFE), "Expected knife to remain after slicing tomato");
        helper.assertTrue(remainingItems.get(1).getDamageValue() == 8, "Expected knife durability to decrease by 1");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void repairingKnivesHasNoCraftingRecipe(GameTestHelper helper) {
        ItemStack firstKnife = new ItemStack(Items.ITEM_KNIFE);
        ItemStack secondKnife = new ItemStack(Items.ITEM_KNIFE);
        firstKnife.setDamageValue(30);
        secondKnife.setDamageValue(70);

        CraftingInput grid = craftingGrid(2, 1, firstKnife, secondKnife);
        Optional<RecipeHolder<CraftingRecipe>> recipe = helper.getLevel()
                .getRecipeManager()
                .getRecipeFor(RecipeType.CRAFTING, grid, helper.getLevel());

        helper.assertTrue(recipe.isEmpty(), "Expected damaged knives to have no crafting repair recipe");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void smeltingRecipeCooksRawPatty(GameTestHelper helper) {
        SmeltingRecipe recipe = requireSmeltingRecipe(helper, recipeId("smelting_item_cooked_patty"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.ITEM_RAW_PATTY));

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Expected smelting recipe to match raw patty input");
        ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_COOKED_PATTY), "Expected smelting recipe to produce cooked patty");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void smokingRecipeCooksRawSausage(GameTestHelper helper) {
        SmokingRecipe recipe = requireSmokingRecipe(helper, recipeId("smoking_item_cooked_sausage"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.ITEM_RAW_SAUSAGE));

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Expected smoking recipe to match raw sausage input");
        ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_COOKED_SAUSAGE), "Expected smoking recipe to produce cooked sausage");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void campfireRecipeLoadsAndCooksPizzaVariant(GameTestHelper helper) {
        CampfireCookingRecipe recipe = requireCampfireRecipe(helper, recipeId("campfire_cooking_pizza_chicken_mushroom_mushroom"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(Items.RAW_PIZZA_CHICKEN_MUSHROOM_MUSHROOM));

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Expected campfire pizza recipe to match raw pizza input");
        ItemStack result = recipe.assemble(input, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.PIZZA_CHICKEN_MUSHROOM_MUSHROOM), "Expected cooked pizza variant result item");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH, timeoutTicks = 500)
    public static void pizzaRecipeWorksEndToEnd(GameTestHelper helper) {
        CraftingRecipe pizzaBaseRecipe = requireCraftingRecipe(helper, recipeId("raw_pizza"));
        CraftingInput pizzaBaseGrid = craftingGrid(3, 2,
                new ItemStack(Items.CHEESE_SLICE), new ItemStack(Items.ITEM_TOMATO_SLICES), ItemStack.EMPTY,
                new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT), new ItemStack(net.minecraft.world.item.Items.WHEAT)
        );

        helper.assertTrue(pizzaBaseRecipe.matches(pizzaBaseGrid, helper.getLevel()), "Expected raw pizza base recipe to match");
        ItemStack rawPizza = pizzaBaseRecipe.assemble(pizzaBaseGrid, helper.getLevel().registryAccess());
        helper.assertTrue(rawPizza.is(Items.RAW_PIZZA), "Expected raw pizza base recipe to produce raw pizza");

        CraftingRecipe toppingRecipe = requireCraftingRecipe(helper, recipeId("raw_pizza_chicken_mushroom"));
        CraftingInput toppingGrid = craftingGrid(3, 3,
                rawPizza.copy(), new ItemStack(net.minecraft.world.item.Items.CHICKEN), new ItemStack(net.minecraft.world.item.Items.BROWN_MUSHROOM),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        helper.assertTrue(toppingRecipe.matches(toppingGrid, helper.getLevel()), "Expected pizza topping recipe to match");
        ItemStack toppedPizza = toppingRecipe.assemble(toppingGrid, helper.getLevel().registryAccess());
        helper.assertTrue(toppedPizza.is(Items.RAW_PIZZA_CHICKEN_MUSHROOM), "Expected topped pizza recipe to produce raw chicken mushroom pizza");

        CampfireCookingRecipe cookingRecipe = requireCampfireRecipe(helper, recipeId("campfire_cooking_pizza_chicken_mushroom"));
        SingleRecipeInput cookingInput = new SingleRecipeInput(toppedPizza.copy());
        helper.assertTrue(cookingRecipe.matches(cookingInput, helper.getLevel()), "Expected campfire recipe to match topped raw pizza");
        ItemStack cookedPizza = cookingRecipe.assemble(cookingInput, helper.getLevel().registryAccess());
        helper.assertTrue(cookedPizza.is(Items.PIZZA_CHICKEN_MUSHROOM), "Expected end-to-end pizza flow to produce cooked chicken mushroom pizza");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teaPouringFillsCupsAndReturnsReducedTeapot(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("tea_pouring"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(Items.ITEM_HOT_TEAPOT_3), new ItemStack(Items.ITEM_TEACUP), new ItemStack(Items.ITEM_TEACUP),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected tea pouring recipe to match teapot plus cups");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_FULL_TEACUP_0), "Expected tea pouring to produce filled teacups");
        helper.assertTrue(result.getCount() == 2, "Expected tea pouring to fill two teacups");

        NonNullList<ItemStack> remainingItems = recipe.getRemainingItems(grid);
        helper.assertTrue(remainingItems.get(0).is(Items.ITEM_HOT_TEAPOT_1), "Expected pouring two cups from a 3-cup teapot to leave a 1-cup hot teapot");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teaSugaringIncreasesSugarLevel(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("tea_sugaring"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(Items.ITEM_FULL_TEACUP_0), new ItemStack(net.minecraft.world.item.Items.SUGAR), new ItemStack(net.minecraft.world.item.Items.SUGAR),
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY,
                ItemStack.EMPTY, ItemStack.EMPTY, ItemStack.EMPTY
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected tea sugaring recipe to match teacup plus sugar");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_FULL_TEACUP_2), "Expected two sugars to produce the two-sugar teacup");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void teaAdvancementOnlyTriggersForHotTeapots(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        AdvancementHolder advancement = requireAdvancement(helper, advancementId("achievements/brew_tea"));

        helper.assertFalse(advancementProgress(player, advancement).isDone(), "Expected brew tea advancement to start locked");

        triggerInventoryChanged(player, new ItemStack(Items.ITEM_TOMATO));
        helper.assertFalse(advancementProgress(player, advancement).isDone(), "Tomato should not trigger the brew tea advancement");

        triggerInventoryChanged(player, new ItemStack(Items.ITEM_HOT_TEAPOT_0));
        helper.assertTrue(advancementProgress(player, advancement).isDone(), "Hot teapot should trigger the brew tea advancement");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void recipeAdvancementUnlocksTomatoSlicingRecipe(GameTestHelper helper) {
        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        ResourceLocation tomatoSlicesRecipeId = recipeId("item_tomato_slices");
        AdvancementHolder advancement = requireAdvancement(helper, advancementId("recipes/item_tomato_slices"));

        helper.assertFalse(advancementProgress(player, advancement).isDone(), "Expected tomato slices recipe advancement to start locked");
        helper.assertFalse(player.getRecipeBook().contains(tomatoSlicesRecipeId), "Expected tomato slices recipe to start locked in the recipe book");

        triggerInventoryChanged(player, new ItemStack(Items.ITEM_TOMATO));

        helper.assertTrue(advancementProgress(player, advancement).isDone(), "Expected tomato item pickup to complete the tomato slices recipe advancement");
        helper.assertTrue(player.getRecipeBook().contains(tomatoSlicesRecipeId), "Expected tomato item pickup to unlock the tomato slices recipe");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH, timeoutTicks = 500)
    public static void brewingProducesTexturedColaExtractAndCraftsCola(GameTestHelper helper) {
        BlockPos brewingStandPos = new BlockPos(1, 1, 1);
        helper.setBlock(brewingStandPos, Blocks.BREWING_STAND);

        BrewingStandBlockEntity stand = helper.getBlockEntity(brewingStandPos);
        stand.setItem(0, PotionContents.createItemStack(net.minecraft.world.item.Items.POTION, Potions.WATER));
        stand.setItem(3, new ItemStack(Items.COLA_POWDER));
        stand.setItem(4, new ItemStack(net.minecraft.world.item.Items.BLAZE_POWDER));
        stand.setChanged();

        helper.runAtTickTime(420, () -> {
            BrewingStandBlockEntity brewedStand = helper.getBlockEntity(brewingStandPos);
            ItemStack brewedExtract = brewedStand.getItem(0).copy();

            helper.assertTrue(brewedExtract.is(Items.COLA_EXTRACT), "Expected brewing stand output to be the cola extract item");

            CraftingRecipe colaRecipe = requireCraftingRecipe(helper, recipeId("cola"));
            CraftingInput colaGrid = craftingGrid(3, 3,
                    new ItemStack(net.minecraft.world.item.Items.SNOWBALL), brewedExtract.copy(), new ItemStack(net.minecraft.world.item.Items.SNOWBALL),
                    new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER),
                    new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER), new ItemStack(Items.CARBONATED_WATER)
            );

            helper.assertTrue(colaRecipe.matches(colaGrid, helper.getLevel()), "Expected cola recipe to accept brewed cola extract");
            ItemStack colaResult = colaRecipe.assemble(colaGrid, helper.getLevel().registryAccess());
            helper.assertTrue(colaResult.is(Items.COLA), "Expected cola recipe to craft cola");
            helper.assertTrue(colaResult.getCount() == 6, "Expected cola recipe to craft 6 cola items");

            NonNullList<ItemStack> remainingItems = colaRecipe.getRemainingItems(colaGrid);
            helper.assertTrue(remainingItems.get(1).is(net.minecraft.world.item.Items.GLASS_BOTTLE),
                    "Expected brewed cola extract input to leave behind a glass bottle");

            helper.succeed();
        });
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void vatBreaksFasterWithPickaxeThanByHand(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, xerca.xercafood.common.block.Blocks.VAT.defaultBlockState());

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        BlockState state = helper.getLevel().getBlockState(pos);

        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
        float handProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        player.getInventory().setItem(player.getInventory().selected, new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE));
        float pickaxeProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        helper.assertTrue(pickaxeProgress > handProgress, "Expected curdling vat to break faster with a pickaxe than by hand");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void vatDropsItselfWithoutAnyTool(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, xerca.xercafood.common.block.Blocks.VAT.defaultBlockState());

        BlockState state = helper.getLevel().getBlockState(pos);
        java.util.List<ItemStack> drops = net.minecraft.world.level.block.Block.getDrops(
                state,
                helper.getLevel(),
                pos,
                helper.getLevel().getBlockEntity(pos),
                null,
                ItemStack.EMPTY
        );

        helper.assertTrue(drops.size() == 1, "Expected curdling vat to have one drop without tools");
        helper.assertTrue(drops.get(0).is(Items.VAT), "Expected curdling vat to drop itself without tools");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = RECIPE_BATCH)
    public static void piesBreakAtCakeSpeed(GameTestHelper helper) {
        BlockPos cakePos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos applePiePos = helper.absolutePos(new BlockPos(2, 2, 1));
        BlockPos berryPiePos = helper.absolutePos(new BlockPos(3, 2, 1));
        net.minecraft.world.level.block.Block applePieBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_apple_pie"));
        net.minecraft.world.level.block.Block berryPieBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK
                .get(ResourceLocation.fromNamespaceAndPath(Mod.MODID, "block_sweet_berry_pie"));

        helper.getLevel().setBlockAndUpdate(cakePos, Blocks.CAKE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(applePiePos, applePieBlock.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(berryPiePos, berryPieBlock.defaultBlockState());

        ServerPlayer player = helper.makeMockServerPlayerInLevel();
        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);

        float cakeProgress = helper.getLevel().getBlockState(cakePos).getDestroyProgress(player, helper.getLevel(), cakePos);
        float applePieProgress = helper.getLevel().getBlockState(applePiePos).getDestroyProgress(player, helper.getLevel(), applePiePos);
        float berryPieProgress = helper.getLevel().getBlockState(berryPiePos).getDestroyProgress(player, helper.getLevel(), berryPiePos);

        helper.assertTrue(Float.compare(applePieProgress, cakeProgress) == 0,
                "Expected apple pie to break at the same speed as cake");
        helper.assertTrue(Float.compare(berryPieProgress, cakeProgress) == 0,
                "Expected sweet berry pie to break at the same speed as cake");
        helper.succeed();
    }
}
