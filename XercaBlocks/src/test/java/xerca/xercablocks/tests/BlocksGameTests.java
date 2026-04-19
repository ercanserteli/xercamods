package xerca.xercablocks.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.StonecutterMenu;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.BlockFunctionalBookcase;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.block_entity.FunctionalBookcaseBlockEntity;
import xerca.xercablocks.item.Items;
import xerca.xercablocks.menu.BookcaseMenu;
import xerca.xercablocks.menu.CarvingStationMenu;
import xerca.xercablocks.recipe.CarvingRecipe;
import xerca.xercablocks.recipe.Recipes;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class BlocksGameTests {
    private static final String BASIC_TEMPLATE = "xercablocks:basic_test";
    private static final String BATCH = "xercablocks_regressions";

    private static ResourceLocation recipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, path);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        return (CraftingRecipe) recipe;
    }

    private static CarvingRecipe requireCarvingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CarvingRecipe, "Expected carving recipe for " + recipeId);
        return (CarvingRecipe) recipe;
    }

    private static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        for (ItemStack stack : stacks) {
            list.add(stack);
        }
        return CraftingInput.of(width, height, list);
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void ropeRecipeCraftsFromThreeString(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("rope/rope"));
        CraftingInput grid = craftingGrid(3, 1,
                new ItemStack(net.minecraft.world.item.Items.STRING),
                new ItemStack(net.minecraft.world.item.Items.STRING),
                new ItemStack(net.minecraft.world.item.Items.STRING)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected rope recipe to match three string");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(Items.ROPE), "Expected rope recipe to produce rope");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void blackTerratileRecipeCraftsFromBlackTerracotta(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("terracotta_tile/black_terratile"));
        CraftingInput grid = craftingGrid(2, 2,
                new ItemStack(net.minecraft.world.item.Items.BLACK_TERRACOTTA), new ItemStack(net.minecraft.world.item.Items.BLACK_TERRACOTTA),
                new ItemStack(net.minecraft.world.item.Items.BLACK_TERRACOTTA), new ItemStack(net.minecraft.world.item.Items.BLACK_TERRACOTTA)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected terratile recipe to match black terracotta");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("black_terratile"))),
                "Expected recipe to produce black terracotta tiles");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvingStationRecipeCutsOakLogIntoCarvedOak(GameTestHelper helper) {
        CarvingRecipe recipe = requireCarvingRecipe(helper, recipeId("carving/carved_oak_1_from_oak_log_carving"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(net.minecraft.world.item.Items.OAK_LOG));

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Expected carving station recipe to accept oak logs");
        helper.assertTrue(recipe.assemble(input, helper.getLevel().registryAccess()).is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("carved_oak_1"))),
                "Expected carving station recipe to produce carved oak");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void craftingCarvingStationConsumesShears(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("carving_station"));
        ItemStack leftShears = new ItemStack(net.minecraft.world.item.Items.SHEARS);
        ItemStack rightShears = new ItemStack(net.minecraft.world.item.Items.SHEARS);
        leftShears.setDamageValue(5);
        rightShears.setDamageValue(8);
        CraftingInput grid = craftingGrid(2, 3,
                leftShears, rightShears,
                new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS),
                new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected carving station recipe to match planks plus shears");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("carving_station"))),
                "Expected carving station recipe to produce carving station");

        var remainingItems = recipe.getRemainingItems(grid);
        helper.assertTrue(remainingItems.stream().allMatch(ItemStack::isEmpty), "Expected carving station recipe to consume the shears instead of returning damaged copies");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void bookcaseMenuRejectsNonBookItems(GameTestHelper helper) {
        FunctionalBookcaseBlockEntity blockEntity = new FunctionalBookcaseBlockEntity(BlockPos.ZERO, Blocks.BLOCK_BOOKCASE.defaultBlockState());
        BookcaseMenu menu = new BookcaseMenu(0, helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL).getInventory(), blockEntity);

        helper.assertTrue(menu.getSlot(0).mayPlace(new ItemStack(net.minecraft.world.item.Items.BOOK)), "Expected book slot to accept books");
        helper.assertTrue(!menu.getSlot(0).mayPlace(new ItemStack(net.minecraft.world.item.Items.DIRT)), "Expected book slot to reject dirt");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void bookcaseStateTracksStoredBooks(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.BLOCK_BOOKCASE.defaultBlockState());

        FunctionalBookcaseBlockEntity blockEntity = (FunctionalBookcaseBlockEntity) helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(blockEntity != null, "Expected bookcase block entity to exist");

        blockEntity.setItem(0, new ItemStack(net.minecraft.world.item.Items.BOOK));
        blockEntity.setItem(1, new ItemStack(net.minecraft.world.item.Items.WRITTEN_BOOK));

        helper.assertTrue(helper.getLevel().getBlockState(pos).getValue(BlockFunctionalBookcase.BOOK_AMOUNT) == 2, "Expected bookcase blockstate to track two stored books");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void terratileBreaksFasterWithPickaxeThanByHand(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockState state = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(Mod.id("black_terratile")).defaultBlockState();
        helper.getLevel().setBlockAndUpdate(pos, state);

        var player = helper.makeMockServerPlayerInLevel();
        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
        float handProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        player.getInventory().setItem(player.getInventory().selected, new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE));
        float pickaxeProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        helper.assertTrue(pickaxeProgress > handProgress, "Expected terracotta tile to break faster with a pickaxe than by hand");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void doubleTerratileSlabDropsTwoItems(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        Block slabBlock = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(Mod.id("black_terratile_slab"));
        BlockState state = slabBlock.defaultBlockState().setValue(net.minecraft.world.level.block.SlabBlock.TYPE, SlabType.DOUBLE);
        helper.getLevel().setBlockAndUpdate(pos, state);

        List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), pos, helper.getLevel().getBlockEntity(pos), null, ItemStack.EMPTY);
        helper.assertTrue(drops.size() == 1, "Expected double terratile slab to produce a single slab stack");
        helper.assertTrue(drops.get(0).is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("black_terratile_slab"))),
                "Expected double terratile slab to drop black terratile slab items");
        helper.assertTrue(drops.get(0).getCount() == 2, "Expected double terratile slab to drop two slab items");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvedWoodBreaksFasterWithAxeThanByHand(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockState state = net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(Mod.id("carved_oak_1")).defaultBlockState();
        helper.getLevel().setBlockAndUpdate(pos, state);

        var player = helper.makeMockServerPlayerInLevel();
        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
        float handProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        player.getInventory().setItem(player.getInventory().selected, new ItemStack(net.minecraft.world.item.Items.IRON_AXE));
        float axeProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        helper.assertTrue(axeProgress > handProgress, "Expected carved wood to break faster with an axe than by hand");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvedWoodDropsItselfWhenBroken(GameTestHelper helper) {
        BlockPos relativePos = new BlockPos(1, 2, 1);
        BlockPos pos = helper.absolutePos(relativePos);
        helper.getLevel().setBlockAndUpdate(pos, net.minecraft.core.registries.BuiltInRegistries.BLOCK.get(Mod.id("carved_oak_1")).defaultBlockState());

        helper.getLevel().destroyBlock(pos, true);
        helper.assertItemEntityPresent(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("carved_oak_1")), relativePos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void ropeDropsItselfWhenBroken(GameTestHelper helper) {
        BlockPos relativePos = new BlockPos(1, 2, 1);
        BlockPos pos = helper.absolutePos(relativePos);
        helper.getLevel().setBlockAndUpdate(pos, Blocks.ROPE.defaultBlockState());

        helper.getLevel().destroyBlock(pos, true);
        helper.assertItemEntityPresent(Items.ROPE, relativePos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void bookcaseDropsBlockAndStoredBooksWhenBroken(GameTestHelper helper) {
        BlockPos relativePos = new BlockPos(1, 2, 1);
        BlockPos pos = helper.absolutePos(relativePos);
        helper.getLevel().setBlockAndUpdate(pos, Blocks.BLOCK_BOOKCASE.defaultBlockState());

        FunctionalBookcaseBlockEntity blockEntity = (FunctionalBookcaseBlockEntity) helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(blockEntity != null, "Expected bookcase block entity to exist");
        blockEntity.setItem(0, new ItemStack(net.minecraft.world.item.Items.BOOK));
        blockEntity.setItem(1, new ItemStack(net.minecraft.world.item.Items.WRITTEN_BOOK));

        helper.getLevel().destroyBlock(pos, true);
        helper.assertItemEntityPresent(Items.ITEM_BOOKCASE, relativePos, 2.0);
        helper.assertItemEntityPresent(net.minecraft.world.item.Items.BOOK, relativePos, 2.0);
        helper.assertItemEntityPresent(net.minecraft.world.item.Items.WRITTEN_BOOK, relativePos, 2.0);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvingRecipesDoNotLoadAsStonecuttingRecipes(GameTestHelper helper) {
        Optional<RecipeHolder<StonecutterRecipe>> stonecutterRecipe = helper.getLevel().getRecipeManager().getRecipeFor(
                RecipeType.STONECUTTING,
                new SingleRecipeInput(new ItemStack(net.minecraft.world.item.Items.OAK_LOG)),
                helper.getLevel()
        );

        Optional<RecipeHolder<CarvingRecipe>> carvingRecipe = helper.getLevel().getRecipeManager().getRecipeFor(
                Recipes.CARVING_TYPE,
                new SingleRecipeInput(new ItemStack(net.minecraft.world.item.Items.OAK_LOG)),
                helper.getLevel()
        );

        helper.assertTrue(stonecutterRecipe.isEmpty(), "Expected carving recipes to be absent from vanilla stonecutting");
        helper.assertTrue(carvingRecipe.isPresent(), "Expected carving recipes to load under xercablocks:carving");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvingStationMenuShowsCarvingRecipesButStonecutterDoesNot(GameTestHelper helper) {
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        CarvingStationMenu carvingMenu = new CarvingStationMenu(0, player.getInventory(), ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(new BlockPos(1, 2, 1))));
        StonecutterMenu stonecutterMenu = new StonecutterMenu(1, player.getInventory(), ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(new BlockPos(2, 2, 1))));

        ItemStack oakLog = new ItemStack(net.minecraft.world.item.Items.OAK_LOG);
        carvingMenu.getSlot(0).container.setItem(0, oakLog.copy());
        carvingMenu.slotsChanged(carvingMenu.getSlot(0).container);
        stonecutterMenu.getSlot(0).container.setItem(0, oakLog.copy());
        stonecutterMenu.slotsChanged(stonecutterMenu.getSlot(0).container);

        helper.assertTrue(carvingMenu.getNumRecipes() > 0, "Expected carving station menu to expose carving recipes");
        helper.assertTrue(stonecutterMenu.getNumRecipes() == 0, "Expected vanilla stonecutter to reject carving recipes");

        helper.assertTrue(carvingMenu.clickMenuButton(player, 0), "Expected carving station menu to accept the first carving recipe selection");
        helper.assertTrue(!carvingMenu.getSlot(1).getItem().isEmpty(),
                "Expected carving station result slot to contain a carving output after selecting a recipe");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvingStationUsesCustomMenuType(GameTestHelper helper) {
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        CarvingStationMenu carvingMenu = new CarvingStationMenu(0, player.getInventory());

        helper.assertTrue(carvingMenu.getType() == xerca.xercablocks.menu.Menus.CARVING_STATION,
                "Expected carving station menu to use the custom xercablocks carving menu type");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvingStationRejectsStonecutterInputsWhileStonecutterAcceptsThem(GameTestHelper helper) {
        var player = helper.makeMockPlayer(net.minecraft.world.level.GameType.SURVIVAL);
        CarvingStationMenu carvingMenu = new CarvingStationMenu(0, player.getInventory(), ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(new BlockPos(1, 2, 1))));
        StonecutterMenu stonecutterMenu = new StonecutterMenu(1, player.getInventory(), ContainerLevelAccess.create(helper.getLevel(), helper.absolutePos(new BlockPos(2, 2, 1))));

        ItemStack stone = new ItemStack(net.minecraft.world.item.Items.STONE);
        carvingMenu.getSlot(0).container.setItem(0, stone.copy());
        carvingMenu.slotsChanged(carvingMenu.getSlot(0).container);
        stonecutterMenu.getSlot(0).container.setItem(0, stone.copy());
        stonecutterMenu.slotsChanged(stonecutterMenu.getSlot(0).container);

        helper.assertTrue(carvingMenu.getNumRecipes() == 0, "Expected carving station to reject vanilla stonecutter inputs like stone");
        helper.assertTrue(stonecutterMenu.getNumRecipes() > 0, "Expected vanilla stonecutter to still show stonecutting recipes for stone");

        helper.assertTrue(stonecutterMenu.clickMenuButton(player, 0), "Expected stonecutter to accept its first stonecutting recipe selection");
        helper.assertTrue(!stonecutterMenu.getSlot(1).getItem().isEmpty(),
                "Expected stonecutter result slot to contain a vanilla stonecutting output after selecting a recipe");
        helper.succeed();
    }
}
