package xerca.xercapaint.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import xerca.xercapaint.item.Items;

import java.util.List;
import java.util.Optional;

import static xerca.xercapaint.Mod.MOD_ID;

public class BaseCraftingGameTests {

    private static final ItemStack STICK = new ItemStack(net.minecraft.world.item.Items.STICK);
    private static final ItemStack PAPER = new ItemStack(net.minecraft.world.item.Items.PAPER);
    private static final ItemStack BRUSH = new ItemStack(net.minecraft.world.item.Items.BRUSH);
    private static final ItemStack PLANKS = new ItemStack(net.minecraft.world.item.Items.OAK_PLANKS);
    private static final ItemStack E = ItemStack.EMPTY;

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, String path) {
        ResourceLocation recipeId = ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, recipeId));
        TestAsserts.assertTrue(helper, recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow(() -> new IllegalStateException("Missing recipe: " + recipeId)).value();
        if (!(recipe instanceof CraftingRecipe craftingRecipe)) {
            throw new IllegalStateException("Expected crafting recipe for " + recipeId);
        }
        return craftingRecipe;
    }

    private static void assertCrafts(GameTestHelper helper, String path, CraftingInput grid, net.minecraft.world.item.Item expected) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, path);
        TestAsserts.assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected " + path + " recipe to match its grid");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        TestAsserts.assertTrue(helper, result.is(expected), "Expected " + path + " recipe to craft " + expected);
    }

    @GameTest
    public void easelCraftsFromSticks(GameTestHelper helper) {
        CraftingInput grid = CraftingInput.of(3, 3, List.of(
                E.copy(), STICK.copy(), E.copy(),
                E.copy(), STICK.copy(), E.copy(),
                STICK.copy(), E.copy(), STICK.copy()));
        assertCrafts(helper, "item_easel", grid, Items.ITEM_EASEL);
        helper.succeed();
    }

    @GameTest
    public void paletteCraftsFromBrushAndPlanks(GameTestHelper helper) {
        CraftingInput grid = CraftingInput.of(3, 2, List.of(
                E.copy(), BRUSH.copy(), E.copy(),
                PLANKS.copy(), PLANKS.copy(), PLANKS.copy()));
        assertCrafts(helper, "item_palette", grid, Items.ITEM_PALETTE);
        helper.succeed();
    }

    @GameTest
    public void smallCanvasCraftsFromPaperAndSticks(GameTestHelper helper) {
        CraftingInput grid = CraftingInput.of(3, 3, List.of(
                STICK.copy(), STICK.copy(), STICK.copy(),
                STICK.copy(), PAPER.copy(), STICK.copy(),
                STICK.copy(), STICK.copy(), STICK.copy()));
        assertCrafts(helper, "item_canvas", grid, Items.ITEM_CANVAS);
        helper.succeed();
    }
}
