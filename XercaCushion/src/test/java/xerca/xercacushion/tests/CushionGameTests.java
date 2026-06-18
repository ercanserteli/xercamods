package xerca.xercacushion.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import xerca.xercacushion.Mod;
import xerca.xercacushion.entity.EntityCushion;
import xerca.xercacushion.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class CushionGameTests {
    private static final String BASIC_TEMPLATE = "xercacushion:basic_test";

    private static ResourceLocation recipeId(String path) {
        return ResourceLocation.fromNamespaceAndPath(Mod.MOD_ID, path);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, ResourceLocation recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().getRecipeManager().byKey(recipeId);
        helper.assertTrue(recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        return (CraftingRecipe) recipe;
    }

    private static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        Collections.addAll(list, stacks);
        return CraftingInput.of(width, height, list);
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void blackCushionRecipeCraftsFromWoolAndFeather(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("black_cushion"));
        CraftingInput grid = craftingGrid(1, 3,
                new ItemStack(net.minecraft.world.item.Items.BLACK_WOOL),
                new ItemStack(net.minecraft.world.item.Items.FEATHER),
                new ItemStack(net.minecraft.world.item.Items.BLACK_WOOL)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected black cushion recipe to match wool-feather-wool");
        helper.assertTrue(recipe.assemble(grid, helper.getLevel().registryAccess()).is(Items.BLACK_CUSHION), "Expected black cushion recipe output");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void allSixteenCushionRecipesLoad(GameTestHelper helper) {
        for (String path : Items.PATHS) {
            helper.assertTrue(helper.getLevel().getRecipeManager().byKey(recipeId(path)).isPresent(), "Missing cushion recipe: " + path);
        }
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void redCushionEntityKeepsItsItemVariant(GameTestHelper helper) {
        EntityCushion cushion = new EntityCushion(helper.getLevel(), 1.5D, 2.0D, 1.5D, Items.RED_CUSHION.getVariant());
        helper.getLevel().addFreshEntity(cushion);

        helper.assertTrue(cushion.getPickResult().is(Items.RED_CUSHION), "Expected red cushion entity to keep its red item variant");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void interactingWithCushionMountsPlayer(GameTestHelper helper) {
        EntityCushion cushion = new EntityCushion(helper.getLevel(), 1.5D, 2.0D, 1.5D, 0);
        helper.getLevel().addFreshEntity(cushion);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(1.5D, 2.0D, 1.5D);

        cushion.interact(player, InteractionHand.MAIN_HAND);

        helper.assertTrue(player.getVehicle() == cushion, "Expected player to mount the cushion");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void unsupportedCushionFallsSlowly(GameTestHelper helper) {
        helper.getLevel().setBlockAndUpdate(new BlockPos(1, 2, 1), Blocks.STONE.defaultBlockState());
        EntityCushion cushion = new EntityCushion(helper.getLevel(), 1.5D, 4.0D, 1.5D, 0);
        helper.getLevel().addFreshEntity(cushion);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(cushion.getY() < 3.5D, "Expected cushion to fall after a few ticks"))
                .thenWaitUntil(() -> helper.assertTrue(cushion.onGround(), "Expected cushion to land on the ground"))
                .thenExecute(() -> helper.assertTrue(Math.abs(cushion.getY() - 3.0D) < 0.0001D, "Expected cushion to sit flush on top of the supporting block, got y=" + cushion.getY()))
                .thenSucceed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void groundedCushionDoesNotSlideFromHorizontalVelocity(GameTestHelper helper) {
        helper.getLevel().setBlockAndUpdate(new BlockPos(1, 1, 1), Blocks.STONE.defaultBlockState());
        EntityCushion cushion = new EntityCushion(helper.getLevel(), 1.5D, 2.0D, 1.5D, 0);
        helper.getLevel().addFreshEntity(cushion);
        cushion.setDeltaMovement(0.35D, 0.0D, 0.2D);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(cushion.onGround(), "Expected cushion to remain grounded"))
                .thenExecute(() -> {
                    helper.assertTrue(Math.abs(cushion.getX() - 1.5D) < 0.01D, "Expected cushion not to slide on X, got x=" + cushion.getX());
                    helper.assertTrue(Math.abs(cushion.getZ() - 1.5D) < 0.01D, "Expected cushion not to slide on Z, got z=" + cushion.getZ());
                })
                .thenSucceed();
    }
}
