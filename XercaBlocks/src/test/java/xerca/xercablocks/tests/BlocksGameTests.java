package xerca.xercablocks.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
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
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.SlabType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
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
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

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

    private static StonecutterRecipe requireStonecuttingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof StonecutterRecipe, "Expected stonecutting recipe for " + recipeId);
        return (StonecutterRecipe) recipe;
    }

    private static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        for (ItemStack stack : stacks) {
            list.add(stack);
        }
        return CraftingInput.of(width, height, list);
    }

    private static Block modBlock(String path) {
        return BuiltInRegistries.BLOCK.get(Mod.id(path));
    }

    private static Item modItem(String path) {
        return BuiltInRegistries.ITEM.get(Mod.id(path));
    }

    private static void assertBreaksFasterWithPickaxeThanByHand(GameTestHelper helper, BlockState state, BlockPos pos, String description) {
        helper.getLevel().setBlockAndUpdate(pos, state);

        var player = helper.makeMockServerPlayerInLevel();
        player.getInventory().setItem(player.getInventory().selected, ItemStack.EMPTY);
        float handProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        player.getInventory().setItem(player.getInventory().selected, new ItemStack(net.minecraft.world.item.Items.IRON_PICKAXE));
        float pickaxeProgress = state.getDestroyProgress(player, helper.getLevel(), pos);

        helper.assertTrue(pickaxeProgress > handProgress, "Expected " + description + " to break faster with a pickaxe than by hand");
    }

    private static BlockState ropePlacementState(GameTestHelper helper, net.minecraft.server.level.ServerPlayer player, BlockPos relativePos) {
        BlockPos absolutePos = helper.absolutePos(relativePos);
        player.getInventory().setItem(player.getInventory().selected, new ItemStack(Items.ROPE));
        BlockHitResult hitResult = new BlockHitResult(Vec3.atCenterOf(absolutePos), Direction.UP, absolutePos, false);
        BlockPlaceContext context = new BlockPlaceContext(new UseOnContext(player, InteractionHand.MAIN_HAND, hitResult));
        return Blocks.ROPE.getStateForPlacement(context);
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
    public static void creativeTabContainsEveryXercaBlocksItem(GameTestHelper helper) {
        Items.BLOCKS_TAB.buildContents(new CreativeModeTab.ItemDisplayParameters(FeatureFlags.DEFAULT_FLAGS, false, helper.getLevel().registryAccess()));

        Set<Item> tabItems = new LinkedHashSet<>();
        for (ItemStack stack : Items.BLOCKS_TAB.getDisplayItems()) {
            tabItems.add(stack.getItem());
        }

        List<Item> modItems = BuiltInRegistries.ITEM.stream()
                .filter(item -> Mod.MOD_ID.equals(BuiltInRegistries.ITEM.getKey(item).getNamespace()))
                .toList();

        helper.assertValueEqual(tabItems.size(), modItems.size(), "Expected the creative tab to contain every registered xercablocks item exactly once");
        for (Item item : modItems) {
            ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item);
            helper.assertTrue(tabItems.contains(item), "Missing creative-tab entry for " + itemId);
        }
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void leatherBlockRecipeCraftsFromNineLeather(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("leather_straw/item_block_leather"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(net.minecraft.world.item.Items.LEATHER), new ItemStack(net.minecraft.world.item.Items.LEATHER), new ItemStack(net.minecraft.world.item.Items.LEATHER),
                new ItemStack(net.minecraft.world.item.Items.LEATHER), new ItemStack(net.minecraft.world.item.Items.LEATHER), new ItemStack(net.minecraft.world.item.Items.LEATHER),
                new ItemStack(net.minecraft.world.item.Items.LEATHER), new ItemStack(net.minecraft.world.item.Items.LEATHER), new ItemStack(net.minecraft.world.item.Items.LEATHER)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected leather block recipe to match nine leather");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(Items.ITEM_BLOCK_LEATHER), "Expected leather block recipe to produce the leather block item");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void strawBlockRecipeCraftsFromNineSugarCane(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("leather_straw/item_block_straw"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE), new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE), new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE),
                new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE), new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE), new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE),
                new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE), new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE), new ItemStack(net.minecraft.world.item.Items.SUGAR_CANE)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected straw block recipe to match nine sugar cane");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(Items.ITEM_BLOCK_STRAW), "Expected straw block recipe to produce the straw block item");
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
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(modItem("black_terratile")),
                "Expected recipe to produce black terracotta tiles");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void blackTerratileSlabRecipeCraftsSixSlabs(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("terracotta_tile/black_terratile_slab"));
        CraftingInput grid = craftingGrid(3, 1,
                new ItemStack(modItem("black_terratile")),
                new ItemStack(modItem("black_terratile")),
                new ItemStack(modItem("black_terratile"))
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected black terratile slab recipe to match three black terratiles");
        ItemStack output = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(output.is(modItem("black_terratile_slab")), "Expected black terratile slab recipe to produce black terratile slabs");
        helper.assertValueEqual(output.getCount(), 6, "Expected black terratile slab recipe to produce six slabs");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void blackTerratileStairsRecipeCraftsFourStairs(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("terracotta_tile/black_terratile_stairs"));
        CraftingInput grid = craftingGrid(3, 3,
                new ItemStack(modItem("black_terratile")), ItemStack.EMPTY, ItemStack.EMPTY,
                new ItemStack(modItem("black_terratile")), new ItemStack(modItem("black_terratile")), ItemStack.EMPTY,
                new ItemStack(modItem("black_terratile")), new ItemStack(modItem("black_terratile")), new ItemStack(modItem("black_terratile"))
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected black terratile stairs recipe to match the staircase pattern");
        ItemStack output = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(output.is(modItem("black_terratile_stairs")), "Expected black terratile stairs recipe to produce black terratile stairs");
        helper.assertValueEqual(output.getCount(), 4, "Expected black terratile stairs recipe to produce four stairs");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void blackTerratileStonecuttingRecipesProduceTileSlabAndStairs(GameTestHelper helper) {
        SingleRecipeInput terracottaInput = new SingleRecipeInput(new ItemStack(net.minecraft.world.item.Items.BLACK_TERRACOTTA));
        StonecutterRecipe tileRecipe = requireStonecuttingRecipe(helper, recipeId("terracotta_tile/black_terratile_from_black_terracotta_stonecutting"));
        helper.assertTrue(tileRecipe.matches(terracottaInput, helper.getLevel()), "Expected black terratile stonecutting recipe to accept black terracotta");
        helper.assertTrue(tileRecipe.assemble(terracottaInput, helper.getLevel().registryAccess()).is(modItem("black_terratile")),
                "Expected black terratile stonecutting recipe to produce black terratile");

        SingleRecipeInput terratileInput = new SingleRecipeInput(new ItemStack(modItem("black_terratile")));
        StonecutterRecipe slabRecipe = requireStonecuttingRecipe(helper, recipeId("terracotta_tile/black_terratile_slab_from_black_terratile_stonecutting"));
        helper.assertTrue(slabRecipe.matches(terratileInput, helper.getLevel()), "Expected black terratile slab stonecutting recipe to accept black terratile");
        ItemStack slabOutput = slabRecipe.assemble(terratileInput, helper.getLevel().registryAccess());
        helper.assertTrue(slabOutput.is(modItem("black_terratile_slab")), "Expected black terratile slab stonecutting recipe to produce black terratile slabs");
        helper.assertValueEqual(slabOutput.getCount(), 2, "Expected black terratile slab stonecutting recipe to produce two slabs");

        StonecutterRecipe stairsRecipe = requireStonecuttingRecipe(helper, recipeId("terracotta_tile/black_terratile_stairs_from_black_terratile_stonecutting"));
        helper.assertTrue(stairsRecipe.matches(terratileInput, helper.getLevel()), "Expected black terratile stairs stonecutting recipe to accept black terratile");
        helper.assertTrue(stairsRecipe.assemble(terratileInput, helper.getLevel().registryAccess()).is(modItem("black_terratile_stairs")),
                "Expected black terratile stairs stonecutting recipe to produce black terratile stairs");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvingStationRecipeCutsOakLogIntoCarvedOak(GameTestHelper helper) {
        CarvingRecipe recipe = requireCarvingRecipe(helper, recipeId("carving/carved_oak_1_from_oak_log_carving"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(net.minecraft.world.item.Items.OAK_LOG));

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Expected carving station recipe to accept oak logs");
        helper.assertTrue(recipe.assemble(input, helper.getLevel().registryAccess()).is(modItem("carved_oak_1")),
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
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(modItem("carving_station")),
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
        assertBreaksFasterWithPickaxeThanByHand(helper, modBlock("black_terratile").defaultBlockState(), pos, "black terracotta tile");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void terratileSlabBreaksFasterWithPickaxeThanByHand(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        assertBreaksFasterWithPickaxeThanByHand(helper, modBlock("black_terratile_slab").defaultBlockState(), pos, "black terracotta tile slab");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void terratileStairsBreaksFasterWithPickaxeThanByHand(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        assertBreaksFasterWithPickaxeThanByHand(helper, modBlock("black_terratile_stairs").defaultBlockState(), pos, "black terracotta tile stairs");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void doubleTerratileSlabDropsTwoItems(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        Block slabBlock = modBlock("black_terratile_slab");
        BlockState state = slabBlock.defaultBlockState().setValue(net.minecraft.world.level.block.SlabBlock.TYPE, SlabType.DOUBLE);
        helper.getLevel().setBlockAndUpdate(pos, state);

        List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), pos, helper.getLevel().getBlockEntity(pos), null, ItemStack.EMPTY);
        helper.assertTrue(drops.size() == 1, "Expected double terratile slab to produce a single slab stack");
        helper.assertTrue(drops.get(0).is(modItem("black_terratile_slab")),
                "Expected double terratile slab to drop black terratile slab items");
        helper.assertTrue(drops.get(0).getCount() == 2, "Expected double terratile slab to drop two slab items");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void blackTerratileLootDropsItself(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockState state = modBlock("black_terratile").defaultBlockState();
        helper.getLevel().setBlockAndUpdate(pos, state);

        List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), pos, null, null, ItemStack.EMPTY);
        helper.assertTrue(drops.size() == 1, "Expected black terratile loot table to produce one stack");
        helper.assertTrue(drops.get(0).is(modItem("black_terratile")), "Expected black terratile loot table to drop black terratile");
        helper.assertValueEqual(drops.get(0).getCount(), 1, "Expected black terratile loot table to drop one block");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void blackTerratileStairsLootDropsItself(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockState state = modBlock("black_terratile_stairs").defaultBlockState();
        helper.getLevel().setBlockAndUpdate(pos, state);

        List<ItemStack> drops = Block.getDrops(state, helper.getLevel(), pos, null, null, ItemStack.EMPTY);
        helper.assertTrue(drops.size() == 1, "Expected black terratile stairs loot table to produce one stack");
        helper.assertTrue(drops.get(0).is(modItem("black_terratile_stairs")), "Expected black terratile stairs loot table to drop black terratile stairs");
        helper.assertValueEqual(drops.get(0).getCount(), 1, "Expected black terratile stairs loot table to drop one block");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvedWoodBreaksFasterWithAxeThanByHand(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockState state = modBlock("carved_oak_1").defaultBlockState();
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
        helper.getLevel().setBlockAndUpdate(pos, modBlock("carved_oak_1").defaultBlockState());

        helper.getLevel().destroyBlock(pos, true);
        helper.assertItemEntityPresent(modItem("carved_oak_1"), relativePos, 2.0);
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
    public static void ropePlacementConnectsToSupportingBlocksAndNeighboringRope(GameTestHelper helper) {
        BlockPos ropePos = new BlockPos(1, 2, 1);
        BlockPos ropeBelowPos = new BlockPos(1, 1, 1);

        var player = helper.makeMockServerPlayerInLevel();
        helper.getLevel().setBlockAndUpdate(helper.absolutePos(ropePos), ropePlacementState(helper, player, ropePos));
        helper.setBlock(new BlockPos(2, 2, 1), net.minecraft.world.level.block.Blocks.STONE);

        helper.assertBlockPresent(Blocks.ROPE, ropePos);
        helper.assertBlockProperty(ropePos, xerca.xercablocks.block.BlockRope.EAST, true);
        helper.assertBlockProperty(ropePos, xerca.xercablocks.block.BlockRope.WEST, false);
        helper.assertBlockProperty(ropePos, xerca.xercablocks.block.BlockRope.NORTH, false);
        helper.assertBlockProperty(ropePos, xerca.xercablocks.block.BlockRope.SOUTH, false);
        helper.assertBlockProperty(ropePos, xerca.xercablocks.block.BlockRope.DOWN, true);

        helper.getLevel().setBlockAndUpdate(helper.absolutePos(ropeBelowPos), ropePlacementState(helper, player, ropeBelowPos));
        helper.assertBlockProperty(ropePos, xerca.xercablocks.block.BlockRope.DOWN, true);
        helper.assertBlockProperty(ropeBelowPos, xerca.xercablocks.block.BlockRope.UP, true);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void ropeIsTaggedClimbableAndPlayersRecognizeIt(GameTestHelper helper) {
        BlockPos ropePos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(ropePos, Blocks.ROPE.defaultBlockState());

        BlockState state = helper.getLevel().getBlockState(ropePos);
        helper.assertTrue(state.is(BlockTags.CLIMBABLE), "Expected rope to be in the minecraft:climbable block tag");

        var player = helper.makeMockServerPlayerInLevel();
        player.setPos(helper.absoluteVec(new Vec3(1.5D, 2.05D, 1.5D)));
        helper.assertTrue(player.onClimbable(), "Expected players standing in rope to treat it as climbable");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void ropePushesAttachedBlocksBecauseItIsSticky(GameTestHelper helper) {
        try {
            var isSticky = net.minecraft.world.level.block.piston.PistonStructureResolver.class.getDeclaredMethod("isSticky", BlockState.class);
            isSticky.setAccessible(true);
            boolean ropeSticky = (boolean) isSticky.invoke(null, Blocks.ROPE.defaultBlockState());
            helper.assertTrue(ropeSticky, "Expected rope to be treated as a sticky block by piston resolution");
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("Unable to inspect piston stickiness", exception);
        }
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void stickyPistonRetractsRopeAndItsAttachedBlock(GameTestHelper helper) {
        try {
            var canStickToEachOther = net.minecraft.world.level.block.piston.PistonStructureResolver.class.getDeclaredMethod("canStickToEachOther", BlockState.class, BlockState.class);
            canStickToEachOther.setAccessible(true);
            boolean ropeSticksToStone = (boolean) canStickToEachOther.invoke(null, Blocks.ROPE.defaultBlockState(), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());
            helper.assertTrue(ropeSticksToStone, "Expected rope to stick to adjacent blocks during piston pull resolution");
        } catch (ReflectiveOperationException exception) {
            throw new RuntimeException("Unable to inspect piston stickiness rules", exception);
        }
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
    public static void bookcasePersistsStoredBooksAcrossBlockEntityReload(GameTestHelper helper) {
        BlockPos pos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(pos, Blocks.BLOCK_BOOKCASE.defaultBlockState());

        FunctionalBookcaseBlockEntity original = (FunctionalBookcaseBlockEntity) helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(original != null, "Expected bookcase block entity to exist before saving");
        original.setItem(0, new ItemStack(net.minecraft.world.item.Items.BOOK));
        original.setItem(5, new ItemStack(net.minecraft.world.item.Items.WRITTEN_BOOK));

        var savedTag = original.saveWithId(helper.getLevel().registryAccess());
        helper.getLevel().getChunkAt(pos).removeBlockEntity(pos);

        BlockEntity reloaded = BlockEntity.loadStatic(pos, helper.getLevel().getBlockState(pos), savedTag, helper.getLevel().registryAccess());
        helper.assertTrue(reloaded instanceof FunctionalBookcaseBlockEntity, "Expected block entity reload to recreate a functional bookcase");
        helper.getLevel().getChunkAt(pos).setBlockEntity(reloaded);

        FunctionalBookcaseBlockEntity restored = (FunctionalBookcaseBlockEntity) helper.getLevel().getBlockEntity(pos);
        helper.assertTrue(restored != null, "Expected restored bookcase block entity to be present after reload");
        helper.assertTrue(restored.getItem(0).is(net.minecraft.world.item.Items.BOOK), "Expected the first saved book to persist across reload");
        helper.assertTrue(restored.getItem(5).is(net.minecraft.world.item.Items.WRITTEN_BOOK), "Expected the second saved book to persist across reload");
        helper.assertTrue(helper.getLevel().getBlockState(pos).getValue(BlockFunctionalBookcase.BOOK_AMOUNT) == 2, "Expected bookcase blockstate to stay in sync after reload");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void bookcaseComparatorOutputTracksStoredBooks(GameTestHelper helper) {
        BlockPos bookcasePos = helper.absolutePos(new BlockPos(1, 2, 1));
        helper.getLevel().setBlockAndUpdate(bookcasePos, Blocks.BLOCK_BOOKCASE.defaultBlockState());

        FunctionalBookcaseBlockEntity bookcase = (FunctionalBookcaseBlockEntity) helper.getLevel().getBlockEntity(bookcasePos);
        helper.assertTrue(bookcase != null, "Expected bookcase block entity to exist for comparator-output test");
        for (int slot = 0; slot < bookcase.getContainerSize(); slot++) {
            bookcase.setItem(slot, new ItemStack(net.minecraft.world.item.Items.BOOK, net.minecraft.world.item.Items.BOOK.getDefaultMaxStackSize()));
        }
        int expectedSignal = net.minecraft.world.inventory.AbstractContainerMenu.getRedstoneSignalFromContainer(bookcase);

        helper.assertValueEqual(expectedSignal, 15, "Expected a fully loaded bookcase to expose the maximum comparator signal");
        helper.assertTrue(helper.getLevel().getBlockState(bookcasePos).getValue(BlockFunctionalBookcase.BOOK_AMOUNT) == 6,
                "Expected the bookcase state used for comparator output to reflect six stored books");
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
