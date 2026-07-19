package xerca.xercapaint.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;
import xerca.xercapaint.item.crafting.RecipeCanvasCloning;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SuppressWarnings("unused")
public class RecipeCanvasCloningGameTests {

    private static final RecipeCanvasCloning RECIPE = RecipeCanvasCloning.INSTANCE;

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

    @GameTest
    public void cloningRejectsGenerationZeroOriginalCanvas(GameTestHelper helper) {
        CraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 0, "gen0"),
                new ItemStack(Items.ITEM_CANVAS)
        );

        TestAsserts.assertTrue(helper, !RECIPE.matches(grid, helper.getLevel()),
                "Expected generation 0 painted canvas to fail matching");
        TestAsserts.assertTrue(helper, RECIPE.assemble(grid).isEmpty(),
                "Expected generation 0 painted canvas to assemble empty");

        helper.succeed();
    }

    @GameTest
    public void cloningStopsAtGenerationThree(GameTestHelper helper) {
        CraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 3, "gen3"),
                new ItemStack(Items.ITEM_CANVAS)
        );

        TestAsserts.assertTrue(helper, RECIPE.matches(grid, helper.getLevel()),
                "Expected generation 3 canvas to pass matching stage");
        TestAsserts.assertTrue(helper, RECIPE.assemble(grid).isEmpty(),
                "Expected generation 3 canvas to assemble empty (clone cap)");

        helper.succeed();
    }

    @GameTest
    public void cloningRejectsDifferentCanvasTypes(GameTestHelper helper) {
        CraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "small"),
                new ItemStack(Items.ITEM_CANVAS_TALL)
        );

        TestAsserts.assertTrue(helper, !RECIPE.matches(grid, helper.getLevel()),
                "Expected cloning to fail for mixed canvas types");
        TestAsserts.assertTrue(helper, RECIPE.assemble(grid).isEmpty(),
                "Expected cloning to assemble empty for mixed canvas types");

        helper.succeed();
    }

    @GameTest
    public void cloningRejectsMixedMaterials(GameTestHelper helper) {
        // A painted paper original with a fresh glass canvas of the same size must not clone
        CraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "paper"),
                new ItemStack(Items.ITEM_CANVAS_GLASS)
        );

        TestAsserts.assertTrue(helper, !RECIPE.matches(grid, helper.getLevel()),
                "Expected cloning to fail for mixed paper/glass canvases");
        TestAsserts.assertTrue(helper, RECIPE.assemble(grid).isEmpty(),
                "Expected cloning to assemble empty for mixed paper/glass canvases");

        helper.succeed();
    }

    @GameTest
    public void cloningGlassCanvasProducesGlassCanvas(GameTestHelper helper) {
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS_GLASS), 1, "glass_clone");
        CraftingInput grid = createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS_GLASS));

        TestAsserts.assertTrue(helper, RECIPE.matches(grid, helper.getLevel()), "Expected glass+glass clone to match");
        ItemStack result = RECIPE.assemble(grid);
        TestAsserts.assertTrue(helper, result.is(Items.ITEM_CANVAS_GLASS), "Expected glass clone result to stay a glass canvas");
        TestAsserts.assertTrue(helper, result.getOrDefault(Items.CANVAS_GENERATION, 0) == 2, "Expected glass clone generation to increase to 2");

        helper.succeed();
    }

    @GameTest
    public void signedCanvasesStackToSixteenBySameGeneration(GameTestHelper helper) {
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "stackable");
        ItemStack clone = RECIPE.assemble(createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS)));
        TestAsserts.assertTrue(helper, !clone.isEmpty(), "Expected the clone recipe to produce a signed canvas");
        TestAsserts.assertTrue(helper, clone.getMaxStackSize() == ItemCanvas.SIGNED_STACK_SIZE,
                "Expected a cloned (signed) canvas to stack up to 16");

        ItemStack cloneAgain = RECIPE.assemble(createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS)));
        TestAsserts.assertTrue(helper, ItemStack.isSameItemSameComponents(clone, cloneAgain),
                "Expected two identical signed canvases of the same generation to be stackable");

        ItemStack signedOriginal = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "stackable");
        ItemCanvas.updateStackSize(signedOriginal);
        TestAsserts.assertTrue(helper, signedOriginal.getMaxStackSize() == ItemCanvas.SIGNED_STACK_SIZE,
                "Expected a signed original canvas to stack up to 16");
        TestAsserts.assertTrue(helper, !ItemStack.isSameItemSameComponents(clone, signedOriginal),
                "Expected signed canvases of different generations to not stack together");

        ItemStack empty1 = new ItemStack(Items.ITEM_CANVAS);
        ItemStack empty2 = new ItemStack(Items.ITEM_CANVAS);
        TestAsserts.assertTrue(helper, empty1.getMaxStackSize() == 1, "Expected an empty canvas to keep stack size 1");
        TestAsserts.assertTrue(helper, ItemStack.isSameItemSameComponents(empty1, empty2),
                "Expected empty canvases to remain stackable with each other");
        helper.succeed();
    }

    @GameTest
    public void cloningConsumesFreshCanvasAndKeepsOriginalAsRemainder(GameTestHelper helper) {
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "original");
        CraftingInput grid = createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS));

        TestAsserts.assertTrue(helper, RECIPE.matches(grid, helper.getLevel()), "Expected valid clone recipe to match");
        ItemStack result = RECIPE.assemble(grid);
        TestAsserts.assertTrue(helper, !result.isEmpty(), "Expected clone result");
        TestAsserts.assertTrue(helper, result.is(Items.ITEM_CANVAS), "Expected clone result type to stay small canvas");
        TestAsserts.assertTrue(helper, result.getOrDefault(Items.CANVAS_GENERATION, 0) == 2, "Expected clone generation to increase to 2");

        NonNullList<ItemStack> remainders = RECIPE.getRemainingItems(grid);
        TestAsserts.assertTrue(helper, remainders.size() == grid.size(), "Expected remainders size to match grid size");
        ItemStack remainingOriginal = remainders.getFirst();
        TestAsserts.assertTrue(helper, remainingOriginal.is(Items.ITEM_CANVAS), "Expected original canvas to remain in its slot");
        TestAsserts.assertTrue(helper, remainingOriginal.getOrDefault(Items.CANVAS_GENERATION, 0) == 1, "Expected remaining original generation to stay 1");
        TestAsserts.assertTrue(helper, "original".equals(remainingOriginal.get(Items.CANVAS_ID)),
                "Expected remaining original metadata to be preserved");
        TestAsserts.assertTrue(helper, remainders.get(1).isEmpty(), "Expected fresh canvas slot remainder to be empty");

        helper.succeed();
    }
}
