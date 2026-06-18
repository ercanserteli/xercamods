package xerca.xercapaint.tests;

import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeFillPalette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeFillPaletteGameTests {
    private static final String BASIC_TEMPLATE = "xercapaint:basic_test";
    private static final String PALETTE_FILL_BATCH = "palette_fill";

    private static final RecipeFillPalette RECIPE = new RecipeFillPalette(
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

    private static int basicIndex(DyeColor color) {
        return 15 - color.getId();
    }

    private static ItemStack createPaletteWithBasicColors(DyeColor... colors) {
        ItemStack palette = new ItemStack(Items.ITEM_PALETTE);
        byte[] basic = new byte[16];
        for (DyeColor color : colors) {
            basic[basicIndex(color)] = 1;
        }
        palette.set(Items.PALETTE_BASIC_COLORS, basic);
        return palette;
    }

    @GameTest(template = BASIC_TEMPLATE, batch = PALETTE_FILL_BATCH)
    public static void fillPaletteAddsNewBasicColorsAndPreservesCustomTag(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        ItemStack palette = createPaletteWithBasicColors(DyeColor.WHITE);
        CompoundTag customTag = new CompoundTag();
        customTag.putString("custom_meta", "keep_me");
        palette.set(DataComponents.CUSTOM_DATA, CustomData.of(customTag));
        items.set(slot(3, 1, 1), palette);
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        items.set(slot(3, 2, 2), new ItemStack(net.minecraft.world.item.Items.BLUE_DYE));
        CraftingInput grid = createGrid(3, 3, items);

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected palette + dyes to match filling recipe");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_PALETTE), "Expected filled palette output");

        byte[] basic = result.getOrDefault(Items.PALETTE_BASIC_COLORS, new byte[0]);
        helper.assertTrue(basic.length == 16, "Expected basic color array length to be 16");
        helper.assertTrue(basic[basicIndex(DyeColor.WHITE)] == 1, "Expected existing white color to remain enabled");
        helper.assertTrue(basic[basicIndex(DyeColor.RED)] == 1, "Expected red color to be enabled");
        helper.assertTrue(basic[basicIndex(DyeColor.BLUE)] == 1, "Expected blue color to be enabled");
        CustomData resultCustomData = result.get(DataComponents.CUSTOM_DATA);
        helper.assertTrue(resultCustomData != null && "keep_me".equals(resultCustomData.copyTag().getString("custom_meta")),
                "Expected custom tag data to be preserved");
        helper.assertTrue(ItemPalette.basicColorCount(result) == 3, "Expected 3 enabled basic colors after filling");
        helper.assertTrue(ItemPalette.basicColorCount(palette) == 1, "Expected input palette stack to remain unchanged");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = PALETTE_FILL_BATCH)
    public static void fillPaletteRejectsAlreadyPresentDye(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        ItemStack palette = createPaletteWithBasicColors(DyeColor.RED);
        items.set(slot(3, 1, 1), palette);
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        CraftingInput grid = createGrid(3, 3, items);

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected recipe to match before duplicate-color validation");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected assembling to fail when dye color is already present");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = PALETTE_FILL_BATCH)
    public static void fillPaletteRejectsUnknownItemsAndNoDye(GameTestHelper helper) {
        List<ItemStack> unknownItems = emptyGrid(3, 3);
        unknownItems.set(slot(3, 1, 1), new ItemStack(Items.ITEM_PALETTE));
        unknownItems.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.STONE));
        CraftingInput unknownItemGrid = createGrid(3, 3, unknownItems);

        helper.assertTrue(!RECIPE.matches(unknownItemGrid, helper.getLevel()), "Expected non-dye ingredient to fail matching");
        helper.assertTrue(RECIPE.assemble(unknownItemGrid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected non-dye ingredient to assemble empty");

        List<ItemStack> noDyeItems = emptyGrid(3, 3);
        noDyeItems.set(slot(3, 1, 1), new ItemStack(Items.ITEM_PALETTE));
        CraftingInput noDyeGrid = createGrid(3, 3, noDyeItems);

        helper.assertTrue(!RECIPE.matches(noDyeGrid, helper.getLevel()), "Expected missing dye to fail matching");
        helper.assertTrue(RECIPE.assemble(noDyeGrid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected missing dye to assemble empty");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = PALETTE_FILL_BATCH)
    public static void fillPaletteDimensionRulesRequireAtLeastTwoByTwo(GameTestHelper helper) {
        helper.assertTrue(!RECIPE.canCraftInDimensions(1, 2), "Expected 1x2 grid to be too small");
        helper.assertTrue(!RECIPE.canCraftInDimensions(2, 1), "Expected 2x1 grid to be too small");
        helper.assertTrue(RECIPE.canCraftInDimensions(2, 2), "Expected 2x2 grid to be valid");
        helper.assertTrue(RECIPE.canCraftInDimensions(3, 3), "Expected 3x3 grid to be valid");
        helper.succeed();
    }
}
