package xerca.xercapaint.tests;

import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import xerca.xercapaint.item.ItemCanvas;
import xerca.xercapaint.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static xerca.xercapaint.Mod.MOD_ID;

public class CanvasRecipeGameTests {
    private static final String BASIC_TEMPLATE = "xercapaint:basic_test";
    private static final String CANVAS_RECIPES_BATCH = "canvas_recipes";

    private record CanvasRecipeSpec(ResourceLocation recipeId, int width, int height, Item expectedResult) {
    }

    private static final CanvasRecipeSpec[] SMALL_CANVAS_RECIPES = {
            new CanvasRecipeSpec(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_canvas_long"), 2, 1, Items.ITEM_CANVAS_LONG),
            new CanvasRecipeSpec(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_canvas_tall"), 1, 2, Items.ITEM_CANVAS_TALL),
            new CanvasRecipeSpec(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_canvas_large"), 2, 2, Items.ITEM_CANVAS_LARGE)
    };

    private static final CanvasRecipeSpec[] SMALL_GLASS_CANVAS_RECIPES = {
            new CanvasRecipeSpec(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_canvas_glass_long"), 2, 1, Items.ITEM_CANVAS_GLASS_LONG),
            new CanvasRecipeSpec(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_canvas_glass_tall"), 1, 2, Items.ITEM_CANVAS_GLASS_TALL),
            new CanvasRecipeSpec(ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_canvas_glass_large"), 2, 2, Items.ITEM_CANVAS_GLASS_LARGE)
    };

    private static CraftingInput createFilledGrid(int width, int height, ItemStack fillStack) {
        List<ItemStack> stacks = new ArrayList<>(width * height);
        for (int i = 0; i < width * height; i++) {
            stacks.add(fillStack.copy());
        }
        return CraftingInput.of(width, height, stacks);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow(() -> new IllegalStateException("Missing recipe: " + recipeId)).value();
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            throw new IllegalStateException("Expected crafting recipe for " + recipeId);
        }
        return craftingRecipe;
    }

    private static List<Integer> emptyPixels(int pixelCount) {
        return new ArrayList<>(Collections.nCopies(pixelCount, 0));
    }

    private static ItemStack createFreshSmallCanvas() {
        return new ItemStack(Items.ITEM_CANVAS);
    }

    private static ItemStack createPaintedSmallCanvas() {
        ItemStack stack = createFreshSmallCanvas();
        ItemCanvas itemCanvas = (ItemCanvas) stack.getItem();
        stack.set(Items.CANVAS_ID, "painted_canvas");
        stack.set(Items.CANVAS_PIXELS, emptyPixels(itemCanvas.getWidth() * itemCanvas.getHeight()));
        stack.set(Items.CANVAS_VERSION, 1);
        return stack;
    }

    private static ItemStack createForeignTaggedFreshSmallCanvas() {
        ItemStack stack = createFreshSmallCanvas();
        CompoundTag tag = new CompoundTag();
        tag.putString("othermod:othertag", "dev");
        stack.set(DataComponents.CUSTOM_DATA, CustomData.of(tag));
        return stack;
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_RECIPES_BATCH)
    public static void smallFreshCanvasesCraftLongTallAndLarge(GameTestHelper helper) {
        for (CanvasRecipeSpec spec : SMALL_CANVAS_RECIPES) {
            CraftingRecipe recipe = requireCraftingRecipe(helper, spec.recipeId());
            CraftingInput grid = createFilledGrid(spec.width(), spec.height(), createFreshSmallCanvas());

            helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected recipe to match for " + spec.recipeId());
            ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
            helper.assertTrue(result.is(spec.expectedResult()), "Expected " + spec.recipeId() + " result item");
        }

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_RECIPES_BATCH)
    public static void smallFreshGlassCanvasesCraftLongTallAndLarge(GameTestHelper helper) {
        ItemStack freshGlass = new ItemStack(Items.ITEM_CANVAS_GLASS);
        for (CanvasRecipeSpec spec : SMALL_GLASS_CANVAS_RECIPES) {
            CraftingRecipe recipe = requireCraftingRecipe(helper, spec.recipeId());
            CraftingInput grid = createFilledGrid(spec.width(), spec.height(), freshGlass);

            helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected glass recipe to match for " + spec.recipeId());
            ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
            helper.assertTrue(result.is(spec.expectedResult()), "Expected " + spec.recipeId() + " result item");
        }

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_RECIPES_BATCH)
    public static void glassCanvasCraftsFromGlassPaneAndSticks(GameTestHelper helper) {
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(MOD_ID, "item_canvas_glass");
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId);

        ItemStack stick = new ItemStack(net.minecraft.world.item.Items.STICK);
        ItemStack pane = new ItemStack(net.minecraft.world.item.Items.GLASS_PANE);
        List<ItemStack> stacks = new ArrayList<>(List.of(
                stick.copy(), stick.copy(), stick.copy(),
                stick.copy(), pane.copy(), stick.copy(),
                stick.copy(), stick.copy(), stick.copy()));
        CraftingInput grid = CraftingInput.of(3, 3, stacks);

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected glass canvas recipe to match");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Items.ITEM_CANVAS_GLASS), "Expected glass canvas result item");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_RECIPES_BATCH)
    public static void paintedCanvasesCannotBeUsedInFreshCanvasRecipes(GameTestHelper helper) {
        ItemStack paintedSmallCanvas = createPaintedSmallCanvas();
        helper.assertTrue(paintedSmallCanvas.get(Items.CANVAS_PIXELS) != null,
                "Expected painted test input to have canvas pixels");

        for (CanvasRecipeSpec spec : SMALL_CANVAS_RECIPES) {
            CraftingRecipe recipe = requireCraftingRecipe(helper, spec.recipeId());
            List<ItemStack> stacks = new ArrayList<>(spec.width() * spec.height());
            for (int i = 0; i < spec.width() * spec.height(); i++) {
                stacks.add(createFreshSmallCanvas());
            }
            stacks.set(0, paintedSmallCanvas.copy());
            CraftingInput grid = CraftingInput.of(spec.width(), spec.height(), stacks);

            helper.assertTrue(!recipe.matches(grid, helper.getLevel()), "Expected painted input to fail matching for " + spec.recipeId());
            helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                    "Expected painted input to assemble empty for " + spec.recipeId());
        }

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CANVAS_RECIPES_BATCH)
    public static void foreignTaggedFreshCanvasesCanBeUsedInFreshCanvasRecipes(GameTestHelper helper) {
        ItemStack foreignTaggedSmallCanvas = createForeignTaggedFreshSmallCanvas();
        helper.assertTrue(foreignTaggedSmallCanvas.get(Items.CANVAS_PIXELS) == null,
                "Expected foreign-tagged fresh canvas to remain fresh (no canvas pixels)");

        for (CanvasRecipeSpec spec : SMALL_CANVAS_RECIPES) {
            CraftingRecipe recipe = requireCraftingRecipe(helper, spec.recipeId());
            CraftingInput grid = createFilledGrid(spec.width(), spec.height(), foreignTaggedSmallCanvas);

            helper.assertTrue(recipe.matches(grid, helper.getLevel()),
                    "Expected foreign-tagged fresh input to match for " + spec.recipeId());
            ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
            helper.assertTrue(result.is(spec.expectedResult()),
                    "Expected foreign-tagged fresh input to craft expected result for " + spec.recipeId());
        }

        helper.succeed();
    }
}
