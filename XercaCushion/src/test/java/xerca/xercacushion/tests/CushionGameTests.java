package xerca.xercacushion.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.gametest.GameTestHolder;
import net.neoforged.neoforge.gametest.PrefixGameTestTemplate;
import xerca.xercacushion.Mod;
import xerca.xercacushion.entity.EntityCushion;
import xerca.xercacushion.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@GameTestHolder(Mod.MOD_ID)
@PrefixGameTestTemplate(false)
public final class CushionGameTests {
    private static final String BASIC_TEMPLATE = "basic_test";

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
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 2.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, Items.RED_CUSHION.getVariant());
        helper.getLevel().addFreshEntity(cushion);

        helper.assertTrue(cushion.getPickResult().is(Items.RED_CUSHION), "Expected red cushion entity to keep its red item variant");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void interactingWithCushionMountsPlayer(GameTestHelper helper) {
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 2.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, 0);
        helper.getLevel().addFreshEntity(cushion);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.moveTo(pos.x, pos.y, pos.z);

        cushion.interact(player, InteractionHand.MAIN_HAND);

        helper.assertTrue(player.getVehicle() == cushion, "Expected player to mount the cushion");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void unsupportedCushionFallsSlowly(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.STONE);
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 4.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, 0);
        helper.getLevel().addFreshEntity(cushion);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(cushion.getY() < pos.y - 0.5D, "Expected cushion to fall after a few ticks"))
                .thenWaitUntil(() -> helper.assertTrue(cushion.onGround(), "Expected cushion to land on the ground"))
                .thenExecute(() -> helper.assertTrue(Math.abs(cushion.getY() - (pos.y - 1.0D)) < 0.0001D, "Expected cushion to sit flush on top of the supporting block, got y=" + cushion.getY()))
                .thenSucceed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void groundedCushionDoesNotSlideFromHorizontalVelocity(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 2.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, 0);
        helper.getLevel().addFreshEntity(cushion);
        cushion.setDeltaMovement(0.35D, 0.0D, 0.2D);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(cushion.onGround(), "Expected cushion to remain grounded"))
                .thenExecute(() -> {
                    helper.assertTrue(Math.abs(cushion.getX() - pos.x) < 0.01D, "Expected cushion not to slide on X, got x=" + cushion.getX());
                    helper.assertTrue(Math.abs(cushion.getZ() - pos.z) < 0.01D, "Expected cushion not to slide on Z, got z=" + cushion.getZ());
                })
                .thenSucceed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void pistonPushesCushionExactlyOneBlock(GameTestHelper helper) {
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 4.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, 0);
        helper.getLevel().addFreshEntity(cushion);

        helper.startSequence()
                .thenExecute(() -> cushion.move(MoverType.PISTON, new Vec3(0.51D, 0.0D, 0.0D)))
                .thenExecuteAfter(1, () -> cushion.move(MoverType.PISTON, new Vec3(0.51D, 0.0D, 0.0D)))
                .thenExecute(() -> helper.assertTrue(Math.abs(cushion.getX() - (pos.x + 1.0D)) < 1.0E-6D,
                        "Expected piston movement to move cushion exactly one block, got " + (cushion.getX() - pos.x)))
                .thenSucceed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void movingCushionDropsWhenItIntersectsAnotherCushion(GameTestHelper helper) {
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 4.0D, 1.5D));
        EntityCushion moving = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, Items.RED_CUSHION.getVariant());
        EntityCushion stationary = new EntityCushion(helper.getLevel(), pos.x + 1.0D, pos.y, pos.z, Items.BLACK_CUSHION.getVariant());
        helper.getLevel().addFreshEntity(moving);
        helper.getLevel().addFreshEntity(stationary);
        moving.move(MoverType.PISTON, new Vec3(0.51D, 0.0D, 0.0D));

        helper.startSequence()
                .thenExecute(() -> {
                    helper.assertTrue(moving.isRemoved(), "Expected the moving cushion to break on intersection");
                    helper.assertTrue(!stationary.isRemoved(), "Expected the stationary cushion to remain");
                    AABB itemSearchBox = stationary.getBoundingBox().inflate(2.0D);
                    boolean droppedMovingVariant = helper.getLevel().getEntitiesOfClass(ItemEntity.class, itemSearchBox).stream()
                            .anyMatch(item -> item.getItem().is(Items.RED_CUSHION));
                    helper.assertTrue(droppedMovingVariant, "Expected the moving cushion to drop its item variant");
                })
                .thenSucceed();
    }
}
