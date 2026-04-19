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
