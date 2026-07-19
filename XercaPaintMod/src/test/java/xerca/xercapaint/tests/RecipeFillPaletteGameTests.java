package xerca.xercapaint.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import xerca.xercapaint.item.ItemPalette;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeFillPalette;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class RecipeFillPaletteGameTests {

    private static final RecipeFillPalette RECIPE = RecipeFillPalette.INSTANCE;

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

    @GameTest
    public void fillPaletteAddsNewBasicColorsAndPreservesCustomTag(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        ItemStack palette = createPaletteWithBasicColors(DyeColor.WHITE);
        CompoundTag customTag = new CompoundTag();
        customTag.putString("custom_meta", "keep_me");
        palette.set(DataComponents.CUSTOM_DATA, CustomData.of(customTag));
        items.set(slot(3, 1, 1), palette);
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.DYE.red()));
        items.set(slot(3, 2, 2), new ItemStack(net.minecraft.world.item.Items.DYE.blue()));
        CraftingInput grid = createGrid(3, 3, items);

        TestAsserts.assertTrue(helper, RECIPE.matches(grid, helper.getLevel()), "Expected palette + dyes to match filling recipe");
        ItemStack result = RECIPE.assemble(grid);
        TestAsserts.assertTrue(helper, result.is(Items.ITEM_PALETTE), "Expected filled palette output");

        byte[] basic = result.getOrDefault(Items.PALETTE_BASIC_COLORS, new byte[0]);
        TestAsserts.assertTrue(helper, basic.length == 16, "Expected basic color array length to be 16");
        TestAsserts.assertTrue(helper, basic[basicIndex(DyeColor.WHITE)] == 1, "Expected existing white color to remain enabled");
        TestAsserts.assertTrue(helper, basic[basicIndex(DyeColor.RED)] == 1, "Expected red color to be enabled");
        TestAsserts.assertTrue(helper, basic[basicIndex(DyeColor.BLUE)] == 1, "Expected blue color to be enabled");
        CustomData resultCustomData = result.get(DataComponents.CUSTOM_DATA);
        TestAsserts.assertTrue(helper, resultCustomData != null && "keep_me".equals(resultCustomData.copyTag().getStringOr("custom_meta", "")),
                "Expected custom tag data to be preserved");
        TestAsserts.assertTrue(helper, ItemPalette.basicColorCount(result) == 3, "Expected 3 enabled basic colors after filling");
        TestAsserts.assertTrue(helper, ItemPalette.basicColorCount(palette) == 1, "Expected input palette stack to remain unchanged");

        helper.succeed();
    }

    @GameTest
    public void fillPaletteRejectsAlreadyPresentDye(GameTestHelper helper) {
        List<ItemStack> items = emptyGrid(3, 3);
        ItemStack palette = createPaletteWithBasicColors(DyeColor.RED);
        items.set(slot(3, 1, 1), palette);
        items.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.DYE.red()));
        CraftingInput grid = createGrid(3, 3, items);

        TestAsserts.assertTrue(helper, RECIPE.matches(grid, helper.getLevel()), "Expected recipe to match before duplicate-color validation");
        TestAsserts.assertTrue(helper, RECIPE.assemble(grid).isEmpty(),
                "Expected assembling to fail when dye color is already present");

        helper.succeed();
    }

    @GameTest
    public void fillPaletteRejectsUnknownItemsAndNoDye(GameTestHelper helper) {
        List<ItemStack> unknownItems = emptyGrid(3, 3);
        unknownItems.set(slot(3, 1, 1), new ItemStack(Items.ITEM_PALETTE));
        unknownItems.set(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.STONE));
        CraftingInput unknownItemGrid = createGrid(3, 3, unknownItems);

        TestAsserts.assertTrue(helper, !RECIPE.matches(unknownItemGrid, helper.getLevel()), "Expected non-dye ingredient to fail matching");
        TestAsserts.assertTrue(helper, RECIPE.assemble(unknownItemGrid).isEmpty(),
                "Expected non-dye ingredient to assemble empty");

        List<ItemStack> noDyeItems = emptyGrid(3, 3);
        noDyeItems.set(slot(3, 1, 1), new ItemStack(Items.ITEM_PALETTE));
        CraftingInput noDyeGrid = createGrid(3, 3, noDyeItems);

        TestAsserts.assertTrue(helper, !RECIPE.matches(noDyeGrid, helper.getLevel()), "Expected missing dye to fail matching");
        TestAsserts.assertTrue(helper, RECIPE.assemble(noDyeGrid).isEmpty(),
                "Expected missing dye to assemble empty");

        helper.succeed();
    }

}
