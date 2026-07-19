package xerca.xercapaint.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ItemStackTemplate;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import xerca.xercapaint.item.Items;

import java.util.List;
import java.util.Optional;

import static xerca.xercapaint.Mod.MOD_ID;

@SuppressWarnings("unused")
public class BaseCraftingGameTests {

    private static final ItemStackTemplate STICK = new ItemStackTemplate(net.minecraft.world.item.Items.STICK);
    private static final ItemStackTemplate PAPER = new ItemStackTemplate(net.minecraft.world.item.Items.PAPER);
    private static final ItemStackTemplate BRUSH = new ItemStackTemplate(net.minecraft.world.item.Items.BRUSH);
    private static final ItemStackTemplate PLANKS = new ItemStackTemplate(net.minecraft.world.item.Items.OAK_PLANKS);
    private static final ItemStack E = ItemStack.EMPTY;

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, String path) {
        Identifier recipeId = Identifier.fromNamespaceAndPath(MOD_ID, path);
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
        ItemStack result = recipe.assemble(grid);
        TestAsserts.assertTrue(helper, result.is(expected), "Expected " + path + " recipe to craft " + expected);
    }

    @GameTest
    public void easelCraftsFromSticks(GameTestHelper helper) {
        CraftingInput grid = CraftingInput.of(3, 3, List.of(
                E.copy(), STICK.create(), E.copy(),
                E.copy(), STICK.create(), E.copy(),
                STICK.create(), E.copy(), STICK.create()));
        assertCrafts(helper, "item_easel", grid, Items.ITEM_EASEL);
        helper.succeed();
    }

    @GameTest
    public void paletteCraftsFromBrushAndPlanks(GameTestHelper helper) {
        CraftingInput grid = CraftingInput.of(3, 2, List.of(
                E.copy(), BRUSH.create(), E.copy(),
                PLANKS.create(), PLANKS.create(), PLANKS.create()));
        assertCrafts(helper, "item_palette", grid, Items.ITEM_PALETTE);
        helper.succeed();
    }

    @GameTest
    public void smallCanvasCraftsFromPaperAndSticks(GameTestHelper helper) {
        CraftingInput grid = CraftingInput.of(3, 3, List.of(
                STICK.create(), STICK.create(), STICK.create(),
                STICK.create(), PAPER.create(), STICK.create(),
                STICK.create(), STICK.create(), STICK.create()));
        assertCrafts(helper, "item_canvas", grid, Items.ITEM_CANVAS);
        helper.succeed();
    }
}
