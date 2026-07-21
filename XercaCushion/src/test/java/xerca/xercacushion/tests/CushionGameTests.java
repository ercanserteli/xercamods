package xerca.xercacushion.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xerca.xercacushion.Mod;
import xerca.xercacushion.entity.EntityCushion;
import xerca.xercacushion.item.Items;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class CushionGameTests {

    private static Identifier recipeId(String path) {
        return Identifier.fromNamespaceAndPath(Mod.MOD_ID, path);
    }

    @SuppressWarnings("DataFlowIssue")
    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, Identifier recipeId) {
        ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId);
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().recipeAccess().byKey(recipeKey);
        helper.assertTrue(recipeOptional.isPresent(), Component.literal("Missing recipe: " + recipeId));
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        helper.assertTrue(recipe instanceof CraftingRecipe, Component.literal("Expected crafting recipe for " + recipeId));
        return (CraftingRecipe) recipe;
    }

    private static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        Collections.addAll(list, stacks);
        return CraftingInput.of(width, height, list);
    }

    public void blackCushionRecipeCraftsFromWoolAndFeather(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("black_cushion"));
        CraftingInput grid = craftingGrid(1, 3,
                new ItemStack(net.minecraft.world.item.Items.BLACK_WOOL),
                new ItemStack(net.minecraft.world.item.Items.FEATHER),
                new ItemStack(net.minecraft.world.item.Items.BLACK_WOOL)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), Component.literal("Expected black cushion recipe to match wool-feather-wool"));
        helper.assertTrue(recipe.assemble(grid).is(Items.BLACK_CUSHION), Component.literal("Expected black cushion recipe output"));
        helper.succeed();
    }

    public void allSixteenCushionRecipesLoad(GameTestHelper helper) {
        for (String path : Items.PATHS) {
            ResourceKey<Recipe<?>> recipeKey = ResourceKey.create(Registries.RECIPE, recipeId(path));
            helper.assertTrue(helper.getLevel().recipeAccess().byKey(recipeKey).isPresent(), Component.literal("Missing cushion recipe: " + path));
        }
        helper.succeed();
    }

    public void redCushionEntityKeepsItsItemVariant(GameTestHelper helper) {
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 2.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, Items.RED_CUSHION.getVariant());
        helper.getLevel().addFreshEntity(cushion);

        helper.assertTrue(cushion.getPickResult().is(Items.RED_CUSHION), Component.literal("Expected red cushion entity to keep its red item variant"));
        helper.succeed();
    }

    public void interactingWithCushionMountsPlayer(GameTestHelper helper) {
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 2.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, 0);
        helper.getLevel().addFreshEntity(cushion);

        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        player.snapTo(pos.x, pos.y, pos.z);

        cushion.interact(player, InteractionHand.MAIN_HAND, pos);

        helper.assertTrue(player.getVehicle() == cushion, Component.literal("Expected player to mount the cushion"));
        helper.succeed();
    }

    public void unsupportedCushionFallsSlowly(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 2, 1), Blocks.STONE);
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 4.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, 0);
        helper.getLevel().addFreshEntity(cushion);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(cushion.getY() < pos.y - 0.5D, Component.literal("Expected cushion to fall after a few ticks")))
                .thenWaitUntil(() -> helper.assertTrue(cushion.onGround(), Component.literal("Expected cushion to land on the ground")))
                .thenExecute(() -> helper.assertTrue(Math.abs(cushion.getY() - (pos.y - 1.0D)) < 0.0001D, Component.literal("Expected cushion to sit flush on top of the supporting block, got y=" + cushion.getY())))
                .thenSucceed();
    }

    public void groundedCushionDoesNotSlideFromHorizontalVelocity(GameTestHelper helper) {
        helper.setBlock(new BlockPos(1, 1, 1), Blocks.STONE);
        Vec3 pos = helper.absoluteVec(new Vec3(1.5D, 2.0D, 1.5D));
        EntityCushion cushion = new EntityCushion(helper.getLevel(), pos.x, pos.y, pos.z, 0);
        helper.getLevel().addFreshEntity(cushion);
        cushion.setDeltaMovement(0.35D, 0.0D, 0.2D);

        helper.startSequence()
                .thenWaitUntil(() -> helper.assertTrue(cushion.onGround(), Component.literal("Expected cushion to remain grounded")))
                .thenExecute(() -> {
                    helper.assertTrue(Math.abs(cushion.getX() - pos.x) < 0.01D, Component.literal("Expected cushion not to slide on X, got x=" + cushion.getX()));
                    helper.assertTrue(Math.abs(cushion.getZ() - pos.z) < 0.01D, Component.literal("Expected cushion not to slide on Z, got z=" + cushion.getZ()));
                })
                .thenSucceed();
    }

    public void pistonPushesCushionExactlyOneBlock(GameTestHelper helper) {
        BlockPos pistonPos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos powerPos = helper.absolutePos(new BlockPos(0, 2, 1));
        BlockPos floorPos = helper.absolutePos(new BlockPos(2, 1, 1));
        helper.getLevel().setBlockAndUpdate(floorPos, Blocks.STONE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(floorPos.east(), Blocks.STONE.defaultBlockState());
        helper.getLevel().setBlockAndUpdate(pistonPos,
                Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.EAST));

        double startX = pistonPos.getX() + 1.5D;
        EntityCushion cushion = new EntityCushion(helper.getLevel(), startX, pistonPos.getY(), pistonPos.getZ() + 0.5D, 0);
        helper.getLevel().addFreshEntity(cushion);
        helper.getLevel().setBlockAndUpdate(powerPos, Blocks.REDSTONE_BLOCK.defaultBlockState());

        helper.startSequence()
                .thenExecuteAfter(5, () -> helper.assertTrue(
                        Math.abs(cushion.getX() - (startX + 1.0D)) < 1.0E-6D,
                        Component.literal("Expected piston to move cushion exactly one block, got " + (cushion.getX() - startX))))
                .thenSucceed();
    }

    public void movingCushionDropsWhenItIntersectsAnotherCushion(GameTestHelper helper) {
        BlockPos pistonPos = helper.absolutePos(new BlockPos(1, 2, 1));
        BlockPos powerPos = helper.absolutePos(new BlockPos(0, 2, 1));
        for (int x = 2; x <= 4; x++) {
            helper.getLevel().setBlockAndUpdate(helper.absolutePos(new BlockPos(x, 1, 1)), Blocks.STONE.defaultBlockState());
        }
        helper.getLevel().setBlockAndUpdate(pistonPos,
                Blocks.PISTON.defaultBlockState().setValue(BlockStateProperties.FACING, Direction.EAST));

        double movingStartX = pistonPos.getX() + 1.5D;
        double y = pistonPos.getY();
        double z = pistonPos.getZ() + 0.5D;
        EntityCushion moving = new EntityCushion(helper.getLevel(), movingStartX, y, z, Items.RED_CUSHION.getVariant());
        EntityCushion stationary = new EntityCushion(helper.getLevel(), movingStartX + 1.0D, y, z, Items.BLACK_CUSHION.getVariant());
        helper.getLevel().addFreshEntity(moving);
        helper.getLevel().addFreshEntity(stationary);
        helper.getLevel().setBlockAndUpdate(powerPos, Blocks.REDSTONE_BLOCK.defaultBlockState());

        helper.startSequence()
                .thenExecuteAfter(5, () -> {
                    helper.assertTrue(moving.isRemoved(), Component.literal("Expected the moving cushion to break on intersection"));
                    helper.assertTrue(!stationary.isRemoved(), Component.literal("Expected the stationary cushion to remain"));
                    AABB itemSearchBox = stationary.getBoundingBox().inflate(2.0D);
                    boolean droppedMovingVariant = helper.getLevel().getEntitiesOfClass(ItemEntity.class, itemSearchBox).stream()
                            .anyMatch(item -> item.getItem().is(Items.RED_CUSHION));
                    helper.assertTrue(droppedMovingVariant, Component.literal("Expected the moving cushion to drop its item variant"));
                })
                .thenSucceed();
    }
}
