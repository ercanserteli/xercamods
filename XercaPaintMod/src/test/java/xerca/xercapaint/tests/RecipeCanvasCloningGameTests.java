package xerca.xercapaint.tests;

import net.minecraft.core.NonNullList;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
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
            new ResourceLocation(xerca.xercapaint.Mod.MOD_ID, "test_canvas_cloning"), CraftingBookCategory.MISC
    );

    private static TestCraftingInput createGrid(int width, int height, ItemStack... input) {
        List<ItemStack> stacks = new ArrayList<>(Collections.nCopies(width * height, ItemStack.EMPTY));
        for (int i = 0; i < input.length && i < stacks.size(); i++) {
            stacks.set(i, input[i]);
        }
        return TestCraftingInput.of(width, height, stacks);
    }

    private static ItemStack createPaintedCanvas(ItemStack canvas, int generation, String name) {
        ItemCanvas itemCanvas = (ItemCanvas) canvas.getItem();
        int pixelCount = itemCanvas.getWidth() * itemCanvas.getHeight();
        CompoundTag tag = canvas.getOrCreateTag();
        tag.putString(ItemCanvas.TAG_CANVAS_ID, name);
        tag.putInt(ItemCanvas.TAG_VERSION, 1);
        tag.putIntArray(ItemCanvas.TAG_PIXELS, new int[pixelCount]);
        tag.putInt(ItemCanvas.TAG_GENERATION, generation);
        return canvas;
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void cloningRejectsGenerationZeroOriginalCanvas(GameTestHelper helper) {
        TestCraftingInput grid = createGrid(
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
        TestCraftingInput grid = createGrid(
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
        TestCraftingInput grid = createGrid(
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
    public static void cloningRejectsMixedMaterials(GameTestHelper helper) {
        // A painted paper original with a fresh glass canvas of the same size must not clone
        TestCraftingInput grid = createGrid(
                2, 2,
                createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "paper"),
                new ItemStack(Items.ITEM_CANVAS_GLASS)
        );

        helper.assertTrue(!RECIPE.matches(grid, helper.getLevel()),
                "Expected cloning to fail for mixed paper/glass canvases");
        helper.assertTrue(RECIPE.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                "Expected cloning to assemble empty for mixed paper/glass canvases");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void cloningGlassCanvasProducesGlassCanvas(GameTestHelper helper) {
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS_GLASS), 1, "glass_clone");
        TestCraftingInput grid = createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS_GLASS));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected glass+glass clone to match");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_CANVAS_GLASS), "Expected glass clone result to stay a glass canvas");
        helper.assertTrue(ItemCanvas.getGeneration(result) == 2, "Expected glass clone generation to increase to 2");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void signedCanvasesStackToSixteenBySameGeneration(GameTestHelper helper) {
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "stackable");
        ItemStack clone = RECIPE.assemble(createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS)),
                helper.getLevel().registryAccess());
        helper.assertTrue(!clone.isEmpty(), "Expected the clone recipe to produce a signed canvas");
        helper.assertTrue(clone.getMaxStackSize() == ItemCanvas.SIGNED_STACK_SIZE,
                "Expected a cloned (signed) canvas to stack up to 16");

        ItemStack cloneAgain = RECIPE.assemble(createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS)),
                helper.getLevel().registryAccess());
        helper.assertTrue(ItemStack.isSameItemSameTags(clone, cloneAgain),
                "Expected two identical signed canvases of the same generation to be stackable");

        ItemStack signedOriginal = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "stackable");
        helper.assertTrue(signedOriginal.getMaxStackSize() == ItemCanvas.SIGNED_STACK_SIZE,
                "Expected a signed original canvas to stack up to 16");
        helper.assertTrue(!ItemStack.isSameItemSameTags(clone, signedOriginal),
                "Expected signed canvases of different generations to not stack together");

        ItemStack empty1 = new ItemStack(Items.ITEM_CANVAS);
        ItemStack empty2 = new ItemStack(Items.ITEM_CANVAS);
        helper.assertTrue(empty1.getMaxStackSize() == 1, "Expected an empty canvas to keep stack size 1");
        helper.assertTrue(ItemStack.isSameItemSameTags(empty1, empty2),
                "Expected empty canvases to remain stackable with each other");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_CLONE_BATCH)
    public static void cloningConsumesFreshCanvasAndKeepsOriginalAsRemainder(GameTestHelper helper) {
        ItemStack original = createPaintedCanvas(new ItemStack(Items.ITEM_CANVAS), 1, "original");
        TestCraftingInput grid = createGrid(2, 2, original.copy(), new ItemStack(Items.ITEM_CANVAS));

        helper.assertTrue(RECIPE.matches(grid, helper.getLevel()), "Expected valid clone recipe to match");
        ItemStack result = RECIPE.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(!result.isEmpty(), "Expected clone result");
        helper.assertTrue(result.is(Items.ITEM_CANVAS), "Expected clone result type to stay small canvas");
        helper.assertTrue(ItemCanvas.getGeneration(result) == 2, "Expected clone generation to increase to 2");

        NonNullList<ItemStack> remainders = RECIPE.getRemainingItems(grid);
        helper.assertTrue(remainders.size() == grid.size(), "Expected remainders size to match grid size");
        ItemStack remainingOriginal = remainders.get(0);
        helper.assertTrue(remainingOriginal.is(Items.ITEM_CANVAS), "Expected original canvas to remain in its slot");
        helper.assertTrue(ItemCanvas.getGeneration(remainingOriginal) == 1, "Expected remaining original generation to stay 1");
        helper.assertTrue("original".equals(remainingOriginal.getOrCreateTag().getString(ItemCanvas.TAG_CANVAS_ID)),
                "Expected remaining original metadata to be preserved");
        helper.assertTrue(remainders.get(1).isEmpty(), "Expected fresh canvas slot remainder to be empty");

        helper.succeed();
    }
}
