package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeCraftPalette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeCraftPaletteGameTests {
    private static final String BASIC_TEMPLATE = "xercapaint:basic_test";
    private static final RecipeCraftPalette RECIPE = new RecipeCraftPalette(
            CraftingBookCategory.MISC
    );

    private static int slot(int width, int row, int col) {
        return row * width + col;
    }

    private static List<ItemStack> emptyGrid(int width, int height) {
        return new ArrayList<>(Collections.nCopies(width * height, ItemStack.EMPTY));
    }

    private static CraftingInput createGrid(int width, int height, List<ItemStack> items) {
        return CraftingInput.of(width, height, items);
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void paletteRecipeAcceptsValidInputs(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        items.set(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.BAMBOO_PLANKS));
        items.set(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.SPRUCE_PLANKS));
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        items.set(slot(3, 2, 2), new ItemStack(net.minecraft.world.item.Items.BLUE_DYE));
        CraftingInput grid = createGrid(3, 3, items);

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected valid plank row + unique dyes to match");

        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_PALETTE), "Expected result to be palette");
        byte[] basic = result.getOrDefault(Items.PALETTE_BASIC_COLORS, new byte[0]);
        helper.assertTrue(basic.length == 16, "Expected palette basic array with 16 entries");
        int redIdx = 15 - DyeColor.RED.getId();
        int blueIdx = 15 - DyeColor.BLUE.getId();
        helper.assertTrue(basic[redIdx] == 1, "Expected red color to be enabled");
        helper.assertTrue(basic[blueIdx] == 1, "Expected blue color to be enabled");
        for (int i = 0; i < 16; i++) {
            if (i != redIdx && i != blueIdx) {
                helper.assertTrue(basic[i] == 0, "Expected non-red non-blue color to be disabled");
            }
        }

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void paletteRecipeRejectsDuplicateDyes(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        items.set(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        items.set(slot(3, 2, 2), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        CraftingInput grid = createGrid(3, 3, items);

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected duplicate dyes to still match recipe");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_PALETTE), "Expected duplicate dyes to still craft a palette");
        byte[] basic = result.getOrDefault(Items.PALETTE_BASIC_COLORS, new byte[0]);
        int redIdx = 15 - DyeColor.RED.getId();
        helper.assertTrue(basic.length == 16, "Expected palette basic array with 16 entries");
        helper.assertTrue(basic[redIdx] == 1, "Expected red to be enabled once for duplicate red dyes");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void paletteRecipeRejectsUnknownItems(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        items.set(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        items.set(slot(3, 2, 1), new ItemStack(net.minecraft.world.item.Items.STONE));
        CraftingInput grid = createGrid(3, 3, items);

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()), "Expected non-plank/non-dye item to fail matching");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(), "Expected invalid grid to assemble empty");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void paletteRecipeRejectsExtraItemInPlankRow(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(4, 3);
        items.set(slot(4, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(4, 1, 1), new ItemStack(net.minecraft.world.item.Items.SPRUCE_PLANKS));
        items.set(slot(4, 1, 2), new ItemStack(net.minecraft.world.item.Items.BIRCH_PLANKS));
        items.set(slot(4, 1, 3), new ItemStack(net.minecraft.world.item.Items.BLUE_DYE));
        items.set(slot(4, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        CraftingInput grid = createGrid(4, 3, items);

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()),
                "Expected recipe to match when plank row has exactly 3 planks");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_PALETTE), "Expected plank-row extra dye scenario to craft a palette");
        byte[] basic = result.getOrDefault(Items.PALETTE_BASIC_COLORS, new byte[0]);
        int redIdx = 15 - DyeColor.RED.getId();
        int blueIdx = 15 - DyeColor.BLUE.getId();
        helper.assertTrue(basic[redIdx] == 1, "Expected red dye outside plank row to be included");
        helper.assertTrue(basic[blueIdx] == 0, "Expected dye inside plank row to be ignored");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = "palette_high_id")
    public static void paletteRecipeRejectsOutOfRangeDyeId(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        items.set(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        items.set(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.SPRUCE_PLANKS));
        items.set(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.BIRCH_PLANKS));
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.WHITE_DYE));
        CraftingInput grid = createGrid(3, 3, items);

        for (DyeColor color : DyeColor.values()) {
            helper.assertTrue(color.getId() >= 0 && color.getId() < 16,
                    "Expected dye id to stay in [0,15] for " + color.getName());
        }
        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected standard white dye to match");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).is(Items.ITEM_PALETTE),
                "Expected standard white dye to craft a palette");
        helper.succeed();
    }
}
