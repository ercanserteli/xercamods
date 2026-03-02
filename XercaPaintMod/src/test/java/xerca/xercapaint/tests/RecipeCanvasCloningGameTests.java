package xerca.xercapaint.tests;

import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CraftingInput;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class RecipeCanvasCloningGameTests {
    private static final String BASIC_TEMPLATE = "xercapaint:basic_test";
    private static final String CANVAS_CLONE_BATCH = "canvas_clone";

    private static final RecipeCanvasCloning RECIPE = new RecipeCanvasCloning(
            CraftingBookCategory.MISC
    );

    private static CraftingInput createGrid(int width, int height, ItemStack... input) {
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(width * height, ItemStack.EMPTY));
        for (int i = 0; i < input.length && i < stacks.size(); i++) {
            stacks.set(i, input[i]);
        }
        return CraftingInput.of(width, height, stacks);
    }

    private static ItemStack createPaintedCanvas(ItemStack canvas, int generation, String name) {
        ItemCanvas itemCanvas = (ItemCanvas) canvas.getItem();
        int pixelCount = itemCanvas.getWidth() * itemCanvas.getHeight();
        canvas.set(Items.CANVAS_ID, name);
        canvas.set(Items.CANVAS_VERSION, 1);
        canvas.set(Items.CANVAS_PIXELS, new ArrayList<>(Collections.nCopies(pixelCount, 0)));
        canvas.set(Items.CANVAS_GENERATION, generation);
        return canvas;
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void cloningRejectsGenerationZeroOriginalCanvas(GameTestHelper helper) {
        CraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 0, "gen0"),
                new ItemStack(Items.ITEM_CANVAS)
        );

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()),
                "Expected generation 0 painted canvas to fail matching");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected generation 0 painted canvas to assemble empty");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void cloningStopsAtGenerationThree(GameTestHelper helper) {
        CraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 3, "gen3"),
                new ItemStack(Items.ITEM_CANVAS)
        );

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()),
                "Expected generation 3 canvas to pass matching stage");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected generation 3 canvas to assemble empty (clone cap)");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void cloningRejectsDifferentCanvasTypes(GameTestHelper helper) {
        CraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "small"),
                new ItemStack(Items.ITEM_CANVAS_TALL)
        );

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()),
                "Expected cloning to fail for mixed canvas types");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected cloning to assemble empty for mixed canvas types");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void cloningConsumesFreshCanvasAndKeepsOriginalAsRemainder(GameTestHelper helper) {
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "original");
        CraftingInput grid = createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected valid clone recipe to match");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(!result.isEmpty(), "Expected clone result");
        helper.assertTrue(result.is(Items.ITEM_CANVAS), "Expected clone result type to stay small canvas");
        helper.assertTrue(result.getOrDefault(Items.CANVAS_GENERATION, 0) == 2, "Expected clone generation to increase to 2");

        NonNullList<ItemStack> remainders = RECIPE.getRemainingItems(grid);
        helper.assertTrue(remainders.size() == grid.size(), "Expected remainders size to match grid size");
        ItemStack remainingOriginal = remainders.get(0);
        helper.assertTrue(remainingOriginal.is(Items.ITEM_CANVAS), "Expected original canvas to remain in its slot");
        helper.assertTrue(remainingOriginal.getOrDefault(Items.CANVAS_GENERATION, 0) == 1, "Expected remaining original generation to stay 1");
        helper.assertTrue("original".equals(remainingOriginal.get(Items.CANVAS_ID)),
                "Expected remaining original metadata to be preserved");
        helper.assertTrue(remainders.get(1).isEmpty(), "Expected fresh canvas slot remainder to be empty");

        helper.succeed();
    }
}
