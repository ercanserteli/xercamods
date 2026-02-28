package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
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
import xerca.xercapaint.common.item.Items;
import xerca.xercapaint.common.item.crafting.RecipeCraftPalette;

import java.lang.reflect.Field;

import static xerca.xercapaint.common.XercaPaint.MODID;

@GameTestHolder(MODID)
public class RecipeCraftPaletteGameTests {
    private static final RecipeCraftPalette RECIPE = new RecipeCraftPalette(
            new ResourceLocation(MODID, "palette_crafting_test"),
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

    @GameTest(template = "basic_test")
    @PrefixGameTestTemplate(false)
    public static void paletteRecipeAcceptsValidInputs(GameTestHelper helper) {
        CraftingContainer grid = createGrid(3, 3);
        grid.setItem(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.BAMBOO_PLANKS));
        grid.setItem(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.SPRUCE_PLANKS));
        grid.setItem(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        grid.setItem(slot(3, 2, 2), new ItemStack(net.minecraft.world.item.Items.BLUE_DYE));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected valid plank row + unique dyes to match");

        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_PALETTE.get()), "Expected result to be palette");
        byte[] basic = result.getOrCreateTag().getByteArray("basic");
        helper.assertTrue(basic.length == 16, "Expected palette basic array with 16 entries");
        int redIdx = 15 - DyeColor.RED.getId();
        int blueIdx =  15 - DyeColor.BLUE.getId();
        helper.assertTrue(basic[redIdx] == 1, "Expected red color to be enabled");
        helper.assertTrue(basic[blueIdx] == 1, "Expected blue color to be enabled");
        for (int i = 0; i < 16; i ++) {
            if (i != redIdx && i != blueIdx) {
                helper.assertTrue(basic[i] == 0, "Expected non-red non-blue color to be disabled");
            }
        }

        helper.succeed();
    }

    @GameTest(template = "basic_test")
    @PrefixGameTestTemplate(false)
    public static void paletteRecipeRejectsDuplicateDyes(GameTestHelper helper) {
        CraftingContainer grid = createGrid(3, 3);
        grid.setItem(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        grid.setItem(slot(3, 2, 2), new ItemStack(net.minecraft.world.item.Items.RED_DYE));

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()), "Expected duplicate dye colors to fail matching");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(), "Expected duplicate dyes to assemble empty");

        helper.succeed();
    }

    @GameTest(template = "basic_test")
    @PrefixGameTestTemplate(false)
    public static void paletteRecipeRejectsUnknownItems(GameTestHelper helper) {
        CraftingContainer grid = createGrid(3, 3);
        grid.setItem(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));
        grid.setItem(slot(3, 2, 1), new ItemStack(net.minecraft.world.item.Items.STONE));

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()), "Expected non-plank/non-dye item to fail matching");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(), "Expected invalid grid to assemble empty");

        helper.succeed();
    }

    @GameTest(template = "basic_test")
    @PrefixGameTestTemplate(false)
    public static void paletteRecipeRejectsExtraItemInPlankRow(GameTestHelper helper) {
        CraftingContainer grid = createGrid(4, 3);
        grid.setItem(slot(4, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(4, 1, 1), new ItemStack(net.minecraft.world.item.Items.SPRUCE_PLANKS));
        grid.setItem(slot(4, 1, 2), new ItemStack(net.minecraft.world.item.Items.BIRCH_PLANKS));
        grid.setItem(slot(4, 1, 3), new ItemStack(net.minecraft.world.item.Items.BLUE_DYE));
        grid.setItem(slot(4, 0, 0), new ItemStack(net.minecraft.world.item.Items.RED_DYE));

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()), "Expected extra item in plank row to fail matching");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(), "Expected invalid plank row to assemble empty");

        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "palette_high_id")
    @PrefixGameTestTemplate(false)
    public static void paletteRecipeRejectsOutOfRangeDyeId(GameTestHelper helper) {
        CraftingContainer grid = createGrid(3, 3);
        grid.setItem(slot(3, 1, 0), new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS));
        grid.setItem(slot(3, 1, 1), new ItemStack(net.minecraft.world.item.Items.SPRUCE_PLANKS));
        grid.setItem(slot(3, 1, 2), new ItemStack(net.minecraft.world.item.Items.BIRCH_PLANKS));
        grid.setItem(slot(3, 0, 0), new ItemStack(net.minecraft.world.item.Items.WHITE_DYE));

        Field idField;
        try {
            idField = DyeColor.class.getDeclaredField("id");
            idField.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            helper.assertTrue(false, "Failed to access DyeColor id field for high-id test: " + e);
            return;
        }

        int originalId = DyeColor.WHITE.getId();
        try {
            idField.setInt(DyeColor.WHITE, 99);
            helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()), "Expected out-of-range dye id to fail matching");
            helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(), "Expected out-of-range dye id to assemble empty");
            helper.succeed();
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to mutate DyeColor id for high-id test: " + e);
        } finally {
            try {
                idField.setInt(DyeColor.WHITE, originalId);
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}
