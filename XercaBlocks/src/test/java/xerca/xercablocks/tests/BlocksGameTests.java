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
import net.minecraft.world.item.crafting.SingleRecipeInput;
import net.minecraft.world.item.crafting.StonecutterRecipe;
import xerca.xercablocks.Mod;
import xerca.xercablocks.block.BlockFunctionalBookcase;
import xerca.xercablocks.block.Blocks;
import xerca.xercablocks.block_entity.FunctionalBookcaseBlockEntity;
import xerca.xercablocks.item.Items;
import xerca.xercablocks.menu.BookcaseMenu;

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
    public static void woodStrippingRecipeDamagesTaggedKnifeTool(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("wood_stripping"));
        helper.assertTrue(Items.ITEM_KNIFE != null, "Expected fallback knife to be registered when XercaFood is absent");
        ItemStack knife = new ItemStack(Items.ITEM_KNIFE);
        knife.setDamageValue(3);
        CraftingInput grid = craftingGrid(2, 1,
                knife,
                new ItemStack(net.minecraft.world.item.Items.OAK_LOG)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected wood stripping recipe to match knife plus oak log");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(net.minecraft.world.item.Items.STRIPPED_OAK_LOG),
                "Expected wood stripping recipe to produce stripped oak log");

        var remainingItems = recipe.getRemainingItems(grid);
        helper.assertTrue(remainingItems.get(0).is(Items.ITEM_KNIFE), "Expected knife to remain after stripping");
        helper.assertTrue(remainingItems.get(0).getDamageValue() == 4, "Expected knife durability to decrease by 1");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void woodCarvingRecipeMapsKnifePatternToVariant(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("wood_carving"));
        helper.assertTrue(Items.ITEM_KNIFE != null, "Expected fallback knife to be registered when XercaFood is absent");
        ItemStack knife = new ItemStack(Items.ITEM_KNIFE);
        knife.setDamageValue(11);
        CraftingInput grid = craftingGrid(2, 2,
                knife, ItemStack.EMPTY,
                ItemStack.EMPTY, new ItemStack(net.minecraft.world.item.Items.STRIPPED_OAK_LOG)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected wood carving recipe to match knife plus stripped oak log");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("carved_oak_8"))),
                "Expected carving pattern to produce carved_oak_8");

        var remainingItems = recipe.getRemainingItems(grid);
        helper.assertTrue(remainingItems.get(0).is(Items.ITEM_KNIFE), "Expected knife to remain after carving");
        helper.assertTrue(remainingItems.get(0).getDamageValue() == 12, "Expected knife durability to decrease by 1");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvingStationRecipeCutsOakLogIntoCarvedOak(GameTestHelper helper) {
        StonecutterRecipe recipe = requireStonecuttingRecipe(helper, recipeId("carving/carved_oak_1_from_oak_log_carving"));
        SingleRecipeInput input = new SingleRecipeInput(new ItemStack(net.minecraft.world.item.Items.OAK_LOG));

        helper.assertTrue(recipe.matches(input, helper.getLevel()), "Expected carving station recipe to accept oak logs");
        helper.assertTrue(recipe.assemble(input, helper.getLevel().registryAccess()).is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("carved_oak_1"))),
                "Expected carving station recipe to produce carved oak");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void craftingCarvingStationConsumesFallbackKnives(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("carved_wood/carving_station"));
        helper.assertTrue(Items.ITEM_KNIFE != null, "Expected fallback knife to be registered when XercaFood is absent");
        ItemStack leftKnife = new ItemStack(Items.ITEM_KNIFE);
        ItemStack rightKnife = new ItemStack(Items.ITEM_KNIFE);
        leftKnife.setDamageValue(5);
        rightKnife.setDamageValue(8);
        CraftingInput grid = craftingGrid(2, 3,
                leftKnife, rightKnife,
                new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS),
                new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected carving station recipe to match planks plus knives");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(net.minecraft.core.registries.BuiltInRegistries.ITEM.get(Mod.id("carving_station"))),
                "Expected carving station recipe to produce carving station");

        var remainingItems = recipe.getRemainingItems(grid);
        helper.assertTrue(remainingItems.stream().allMatch(ItemStack::isEmpty), "Expected carving station recipe to consume the knives instead of returning damaged copies");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void fallbackKnifeRecipeCraftsFromIronAndStick(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("item_knife"));
        CraftingInput grid = craftingGrid(1, 2,
                new ItemStack(net.minecraft.world.item.Items.IRON_INGOT),
                new ItemStack(net.minecraft.world.item.Items.STICK)
        );

        helper.assertTrue(Items.ITEM_KNIFE != null, "Expected fallback knife to be registered when XercaFood is absent");
        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected knife recipe to match iron ingot over stick");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(Items.ITEM_KNIFE),
                "Expected knife recipe to produce the fallback knife");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void fallbackKnifeHasNoGlobalCraftingRemainder(GameTestHelper helper) {
        helper.assertTrue(Items.ITEM_KNIFE != null, "Expected fallback knife to be registered when XercaFood is absent");
        ItemStack knife = new ItemStack(Items.ITEM_KNIFE);
        knife.setDamageValue(9);

        helper.assertTrue(knife.getItem().getRecipeRemainder(knife).isEmpty(),
                "Expected fallback knife to avoid a global crafting remainder so normal crafting and repair consume it");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = BATCH)
    public static void carvedCrimsonStemDoesNotEmitLight(GameTestHelper helper) {
        helper.assertTrue(Blocks.CARVED_WOODS.get("carved_crimson_1").defaultBlockState().getLightEmission() == 0,
                "Expected carved crimson stem to stay non-luminous");
        helper.assertTrue(Blocks.CARVED_WOODS.get("carved_warped_1").defaultBlockState().getLightEmission() == 0,
                "Expected carved warped stem to remain non-luminous");
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
}
