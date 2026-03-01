package xerca.xercapaint.tests;

import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.TransientCraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import xerca.xercapaint.common.item.ItemCanvas;
import xerca.xercapaint.common.item.Items;

import java.util.Optional;

import static xerca.xercapaint.common.XercaPaint.MODID;

@GameTestHolder(MODID)
public class CanvasRecipeGameTests {
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

    private record CanvasRecipeSpec(ResourceLocation recipeId, int width, int height, Item expectedResult) {
    }

    private static final CanvasRecipeSpec[] SMALL_CANVAS_RECIPES = new CanvasRecipeSpec[] {
            new CanvasRecipeSpec(new ResourceLocation(MODID, "item_canvas_long"), 2, 1, Items.ITEM_CANVAS_LONG.get()),
            new CanvasRecipeSpec(new ResourceLocation(MODID, "item_canvas_tall"), 1, 2, Items.ITEM_CANVAS_TALL.get()),
            new CanvasRecipeSpec(new ResourceLocation(MODID, "item_canvas_large"), 2, 2, Items.ITEM_CANVAS_LARGE.get())
    };

    private static CraftingContainer createGrid(int width, int height) {
        return new TransientCraftingContainer(new DummyMenu(), width, height);
    }

    private static void fillGrid(CraftingContainer grid, ItemStack stack) {
        for (int i = 0; i < grid.getContainerSize(); i++) {
            grid.setItem(i, stack.copy());
        }
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<? extends Recipe<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow(() -> new IllegalStateException("Missing recipe: " + recipeId));
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            throw new IllegalStateException("Expected crafting recipe for " + recipeId);
        }
        return craftingRecipe;
    }

    private static ItemStack createFreshSmallCanvas() {
        return new ItemStack(Items.ITEM_CANVAS.get());
    }

    private static ItemStack createPaintedSmallCanvas() {
        ItemStack stack = createFreshSmallCanvas();
        ItemCanvas itemCanvas = (ItemCanvas) stack.getItem();
        stack.getOrCreateTag().putString("name", "painted_canvas");
        stack.getOrCreateTag().putIntArray("pixels", new int[itemCanvas.getWidth() * itemCanvas.getHeight()]);
        return stack;
    }

    private static ItemStack createForeignTaggedFreshSmallCanvas() {
        ItemStack stack = createFreshSmallCanvas();
        stack.getOrCreateTag().putString("othermod:othertag", "dev");
        return stack;
    }

    @GameTest(template = "basic_test", batch = "canvas_recipes")
    @PrefixGameTestTemplate(false)
    public static void smallFreshCanvasesCraftLongTallAndLarge(GameTestHelper helper) {
        for (CanvasRecipeSpec spec : SMALL_CANVAS_RECIPES) {
            CraftingRecipe recipe = requireCraftingRecipe(helper, spec.recipeId());
            CraftingContainer grid = createGrid(spec.width(), spec.height());
            fillGrid(grid, createFreshSmallCanvas());

            helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected recipe to match for " + spec.recipeId());
            ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
            helper.assertTrue(result.is(spec.expectedResult()), "Expected " + spec.recipeId() + " result item");
        }

        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "canvas_recipes")
    @PrefixGameTestTemplate(false)
    public static void paintedCanvasesCannotBeUsedInFreshCanvasRecipes(GameTestHelper helper) {
        ItemStack paintedSmallCanvas = createPaintedSmallCanvas();
        helper.assertTrue(ItemCanvas.hasCanvasData(paintedSmallCanvas), "Expected painted test input to be recognized as canvas data");

        for (CanvasRecipeSpec spec : SMALL_CANVAS_RECIPES) {
            CraftingRecipe recipe = requireCraftingRecipe(helper, spec.recipeId());
            CraftingContainer grid = createGrid(spec.width(), spec.height());
            fillGrid(grid, createFreshSmallCanvas());
            grid.setItem(0, paintedSmallCanvas.copy());

            helper.assertTrue(!recipe.matches(grid, helper.getLevel()), "Expected painted input to fail matching for " + spec.recipeId());
            helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).isEmpty(),
                    "Expected painted input to assemble empty for " + spec.recipeId());
        }

        helper.succeed();
    }

    @GameTest(template = "basic_test", batch = "canvas_recipes")
    @PrefixGameTestTemplate(false)
    public static void foreignTaggedFreshCanvasesCanBeUsedInFreshCanvasRecipes(GameTestHelper helper) {
        ItemStack foreignTaggedSmallCanvas = createForeignTaggedFreshSmallCanvas();
        helper.assertTrue(!ItemCanvas.hasCanvasData(foreignTaggedSmallCanvas),
                "Expected foreign-tagged fresh canvas to remain fresh");

        for (CanvasRecipeSpec spec : SMALL_CANVAS_RECIPES) {
            CraftingRecipe recipe = requireCraftingRecipe(helper, spec.recipeId());
            CraftingContainer grid = createGrid(spec.width(), spec.height());
            fillGrid(grid, foreignTaggedSmallCanvas);

            helper.assertTrue(recipe.matches(grid, helper.getLevel()),
                    "Expected foreign-tagged fresh input to match for " + spec.recipeId());
            ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
            helper.assertTrue(result.is(spec.expectedResult()),
                    "Expected foreign-tagged fresh input to craft expected result for " + spec.recipeId());
        }

        helper.succeed();
    }
}
