package xerca.xercapaint.tests;

import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.WrittenBookItem;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;

import static xerca.xercapaint.Mod.MOD_ID;

public class RecipeCanvasCloningGameTests {
    private static final RecipeCanvasCloning RECIPE = new RecipeCanvasCloning(
            new ResourceLocation(MOD_ID, "canvas_cloning_bounds_test"),
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

    private static ItemStack createPaintedCanvas(ItemStack canvas, int generation, String name) {
        ItemCanvas itemCanvas = (ItemCanvas) canvas.getItem();
        int pixelCount = itemCanvas.getWidth() * itemCanvas.getHeight();
        canvas.getOrCreateTag().putString("name", name);
        canvas.getOrCreateTag().putIntArray("pixels", new int[pixelCount]);
        canvas.getOrCreateTag().putInt("generation", generation);
        return canvas;
    }

    @GameTest(template = "xercapaint:basic_test", batch = "canvas_clone")
    public static void cloningRejectsGenerationZeroOriginalCanvas(GameTestHelper helper) {
        CraftingContainer grid = createGrid(2, 2);
        grid.setItem(0, createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 0, "gen0"));
        grid.setItem(1, new ItemStack(Items.ITEM_CANVAS));

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()),
                "Expected generation 0 painted canvas to fail matching");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected generation 0 painted canvas to assemble empty");

        helper.succeed();
    }

    @GameTest(template = "xercapaint:basic_test", batch = "canvas_clone")
    public static void cloningStopsAtGenerationThree(GameTestHelper helper) {
        CraftingContainer grid = createGrid(2, 2);
        grid.setItem(0, createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 3, "gen3"));
        grid.setItem(1, new ItemStack(Items.ITEM_CANVAS));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()),
                "Expected generation 3 canvas to pass matching stage");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected generation 3 canvas to assemble empty (clone cap)");

        helper.succeed();
    }

    @GameTest(template = "xercapaint:basic_test", batch = "canvas_clone")
    public static void cloningRejectsDifferentCanvasTypes(GameTestHelper helper) {
        CraftingContainer grid = createGrid(2, 2);
        grid.setItem(0, createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "small"));
        grid.setItem(1, new ItemStack(Items.ITEM_CANVAS_TALL));

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()),
                "Expected cloning to fail for mixed canvas types");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected cloning to assemble empty for mixed canvas types");

        helper.succeed();
    }

    @GameTest(template = "xercapaint:basic_test", batch = "canvas_clone")
    public static void cloningConsumesFreshCanvasAndKeepsOriginalAsRemainder(GameTestHelper helper) {
        CraftingContainer grid = createGrid(2, 2);
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "original");
        grid.setItem(0, original.copy());
        grid.setItem(1, new ItemStack(Items.ITEM_CANVAS));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected valid clone recipe to match");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(!result.isEmpty(), "Expected clone result");
        helper.assertTrue(result.is(Items.ITEM_CANVAS), "Expected clone result type to stay small canvas");
        helper.assertTrue(WrittenBookItem.getGeneration(result) == 2, "Expected clone generation to increase to 2");

        NonNullList<ItemStack> remainders = RECIPE.getRemainingItems(grid);
        helper.assertTrue(remainders.size() == grid.getContainerSize(), "Expected remainders size to match grid size");
        ItemStack remainingOriginal = remainders.get(0);
        helper.assertTrue(remainingOriginal.is(Items.ITEM_CANVAS), "Expected original canvas to remain in its slot");
        helper.assertTrue(WrittenBookItem.getGeneration(remainingOriginal) == 1, "Expected remaining original generation to stay 1");
        helper.assertTrue("original".equals(remainingOriginal.getOrCreateTag().getString("name")),
                "Expected remaining original metadata to be preserved");
        helper.assertTrue(remainders.get(1).isEmpty(), "Expected fresh canvas slot remainder to be empty");

        helper.succeed();
    }
}
