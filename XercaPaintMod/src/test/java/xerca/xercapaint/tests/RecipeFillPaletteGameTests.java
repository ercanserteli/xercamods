package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import xerca.xercapaint.common.item.ItemPalette;
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.item.crafting.RecipeFillPalette;

import static xerca.xercapaint.common.XercaPaint.MODID;

@GameTestHolder(MODID)
public class RecipeFillPaletteGameTests {
    private static final RecipeFillPalette RECIPE = new RecipeFillPalette(
            new ResourceLocation(MODID, "palette_filling_test"),
            CraftingBookCategory.MISC
    );

    private static final class DummyMenu extends AbstractContainerMenu {
        private DummyMenu() {
            super(null, -1);
        }

        @Override
        public ItemStack quickMoveStack(Player player, int slotId) {
            return ItemStack.EMPTY;
        }

        @Override
        public boolean stillValid(Player player) {
            return true;
        }
    }

    private static CraftingContainer createGrid(int width, int height) {
        return new TransientCraftingContainer(new DummyMenu(), width, height);
    }

    private static int slot(int width, int row, int col) {
        return row * width + col;
    }

    private static int basicIndex(DyeColor color) {
        return 15 - color.getId();
    }

    private static ItemStack createPaletteWithBasicColors(DyeColor... colors) {
        ItemStack palette = new ItemStack(Items.ITEM_PALETTE.get());
        byte[] basic = new byte[16];
        for (DyeColor color : colors) {
            basic[basicIndex(color)] = 1;
        }
        palette.getOrCreateTag().putByteArray("basic", basic);
        return palette;
    }

    @GameTest(template = "basic_test", batch = "palette_fill")
    @PrefixGameTestTemplate(false)
    public static void fillPaletteAddsNewBasicColorsAndPreservesCustomTag(GameTestHelper helper) {
        CraftingContainer grid = createGrid(3, 3);
        ItemStack palette = createPaletteWithBasicColors(DyeColor.WHITE);
        CompoundTag orgTag = palette.getOrCreateTag();
        orgTag.putString("custom_meta", "keep_me");
        grid.setItem(slot(3, 1, 1), palette);
        grid.setItem(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        grid.setItem(slot(3, 2, 2), new ItemStack(net.minecraft.world.item.Items.BLUE_DYE));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected palette + dyes to match filling recipe");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_PALETTE.get()), "Expected filled palette output");

        byte[] basic = result.getOrCreateTag().getByteArray("basic");
        helper.assertTrue(basic.length == 16, "Expected basic color array length to be 16");
        helper.assertTrue(basic[basicIndex(DyeColor.WHITE)] == 1, "Expected existing white color to remain enabled");
        helper.assertTrue(basic[basicIndex(DyeColor.RED)] == 1, "Expected red color to be enabled");
        helper.assertTrue(basic[basicIndex(DyeColor.BLUE)] == 1, "Expected blue color to be enabled");
        helper.assertTrue("keep_me".equals(result.getOrCreateTag().getString("custom_meta")), "Expected custom tag data to be preserved");
        helper.assertTrue(ItemPalette.basicColorCount(result) == 3, "Expected 3 enabled basic colors after filling");
        helper.assertTrue(ItemPalette.basicColorCount(palette) == 1, "Expected input palette stack to remain unchanged");

        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "palette_fill")
    @PrefixGameTestTemplate(false)
    public static void fillPaletteRejectsAlreadyPresentDye(GameTestHelper helper) {
        CraftingContainer grid = createGrid(3, 3);
        ItemStack palette = createPaletteWithBasicColors(DyeColor.RED);
        grid.setItem(slot(3, 1, 1), palette);
        grid.setItem(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected recipe to match before duplicate-color validation");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected assembling to fail when dye color is already present");

        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "palette_fill")
    @PrefixGameTestTemplate(false)
    public static void fillPaletteRejectsUnknownItemsAndNoDye(GameTestHelper helper) {
        CraftingContainer unknownItemGrid = createGrid(3, 3);
        unknownItemGrid.setItem(slot(3, 1, 1), new ItemStack(Items.ITEM_PALETTE.get()));
        unknownItemGrid.setItem(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.STONE));

        helper.assertTrue(!RECIPE.matches(unknownItemGrid, helper.getLevel()), "Expected non-dye ingredient to fail matching");
        helper.assertTrue(RECIPE.assemble(unknownItemGrid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected non-dye ingredient to assemble empty");

        CraftingContainer noDyeGrid = createGrid(3, 3);
        noDyeGrid.setItem(slot(3, 1, 1), new ItemStack(Items.ITEM_PALETTE.get()));

        helper.assertTrue(!RECIPE.matches(noDyeGrid, helper.getLevel()), "Expected missing dye to fail matching");
        helper.assertTrue(RECIPE.assemble(noDyeGrid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected missing dye to assemble empty");

        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "palette_fill")
    @PrefixGameTestTemplate(false)
    public static void fillPaletteDimensionRulesRequireAtLeastTwoByTwo(GameTestHelper helper) {
        helper.assertTrue(!RECIPE.canCraftInDimensions(1, 2), "Expected 1x2 grid to be too small");
        helper.assertTrue(!RECIPE.canCraftInDimensions(2, 1), "Expected 2x1 grid to be too small");
        helper.assertTrue(RECIPE.canCraftInDimensions(2, 2), "Expected 2x2 grid to be valid");
        helper.assertTrue(RECIPE.canCraftInDimensions(3, 3), "Expected 3x3 grid to be valid");
        helper.succeed();
    }
}
