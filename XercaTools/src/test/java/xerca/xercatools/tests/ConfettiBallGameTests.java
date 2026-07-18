package xerca.xercatools.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.CraftingRecipe;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.block.entity.DispenserBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.Mod;
import xerca.xercatools.entity.EntityConfettiBall;
import xerca.xercatools.item.Items;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class ConfettiBallGameTests {
    private static final Method DISPENSE_METHOD;

    static {
        try {
            DISPENSE_METHOD = DispenserBlock.class.getDeclaredMethod("getDispenseMethod", Level.class, ItemStack.class);
            DISPENSE_METHOD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static EntityConfettiBall requireSingleBallNear(GameTestHelper helper, BlockPos relativePos, String message) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(3.0D, 3.0D, 3.0D);
        List<EntityConfettiBall> balls = helper.getLevel().getEntitiesOfClass(EntityConfettiBall.class, searchBox, Entity::isAlive);
        TestAsserts.assertTrue(helper, !balls.isEmpty(), message);
        return balls.getFirst();
    }

    private static Identifier recipeId(String path) {
        return Identifier.fromNamespaceAndPath(xerca.xercatools.Mod.MOD_ID, path);
    }

    private static CraftingRecipe requireCraftingRecipe(GameTestHelper helper, Identifier recipeId) {
        Optional<RecipeHolder<?>> recipeOptional = helper.getLevel().recipeAccess().byKey(ResourceKey.create(Registries.RECIPE, recipeId));
        TestAsserts.assertTrue(helper, recipeOptional.isPresent(), "Missing recipe: " + recipeId);
        Recipe<?> recipe = recipeOptional.orElseThrow().value();
        TestAsserts.assertTrue(helper, recipe instanceof CraftingRecipe, "Expected crafting recipe for " + recipeId);
        assert recipe instanceof CraftingRecipe;
        return (CraftingRecipe) recipe;
    }

    private static CraftingInput craftingGrid(int width, int height, ItemStack... stacks) {
        List<ItemStack> list = new ArrayList<>(stacks.length);
        Collections.addAll(list, stacks);
        return CraftingInput.of(width, height, list);
    }

    private static DispenseItemBehavior getDispenseMethod(GameTestHelper helper, DispenserBlock dispenserBlock, ItemStack stack) {
        try {
            return (DispenseItemBehavior) DISPENSE_METHOD.invoke(dispenserBlock, helper.getLevel(), stack);
        } catch (ReflectiveOperationException e) {
            TestAsserts.assertTrue(helper, false, "Failed to access dispenser behavior for confetti ball: " + e);
            return DispenseItemBehavior.NOOP;
        }
    }

    private static DispenserBlockEntity requireDispenser(GameTestHelper helper, BlockPos absolutePos) {
        if (helper.getLevel().getBlockEntity(absolutePos) instanceof DispenserBlockEntity dispenser) {
            return dispenser;
        }
        TestAsserts.assertTrue(helper, false, "Expected a dispenser block entity at the test position");
        throw new IllegalStateException("Unreachable after GameTest assertion failure");
    }

    @GameTest
    public void playerUseSpawnsConfettiBallAndConsumesOneItem(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        BlockPos playerPos = helper.absolutePos(new BlockPos(1, 2, 1));
        player.snapTo(Vec3.atCenterOf(playerPos));
        player.setYRot(180.0F);
        player.setXRot(0.0F);

        ItemStack stack = new ItemStack(Items.CONFETTI_BALL, 2);
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);

        Items.CONFETTI_BALL.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);

        TestAsserts.assertTrue(helper, player.getMainHandItem().getCount() == 1, "Expected player throw to consume exactly one confetti ball");
        EntityConfettiBall ball = requireSingleBallNear(helper, new BlockPos(1, 2, 1), "Expected player use to spawn a confetti ball entity");
        TestAsserts.assertTrue(helper, ball.getItem().is(Items.CONFETTI_BALL), "Expected spawned confetti ball to keep the synced confetti ball item");
        TestAsserts.assertTrue(helper, ball.getOwner() == player, "Expected thrown confetti ball to track the player as its owner");
        helper.succeed();
    }

    @GameTest
    public void dispenserUseSpawnsConfettiBallWithoutSynchedDataCrash(GameTestHelper helper) {
        BlockPos dispenserPos = new BlockPos(1, 2, 1);
        BlockPos absolutePos = helper.absolutePos(dispenserPos);
        BlockState state = net.minecraft.world.level.block.Blocks.DISPENSER.defaultBlockState()
                .setValue(DispenserBlock.FACING, Direction.NORTH);
        helper.getLevel().setBlockAndUpdate(absolutePos, state);

        DispenserBlockEntity dispenser = requireDispenser(helper, absolutePos);
        ItemStack stack = new ItemStack(Items.CONFETTI_BALL);
        dispenser.setItem(0, stack);

        DispenseItemBehavior behavior = getDispenseMethod(helper, (DispenserBlock) state.getBlock(), stack);
        ItemStack remaining = behavior.dispense(new BlockSource(helper.getLevel(), absolutePos, state, dispenser), stack);

        TestAsserts.assertTrue(helper, remaining.isEmpty() || remaining.getCount() == 0, "Expected dispenser launch to consume the loaded confetti ball");
        EntityConfettiBall ball = requireSingleBallNear(helper, dispenserPos.relative(Direction.NORTH), "Expected dispenser use to spawn a confetti ball entity");
        TestAsserts.assertTrue(helper, ball.getItem().is(Items.CONFETTI_BALL), "Expected dispenser-spawned confetti ball to have synced item data");
        helper.succeed();
    }

    @GameTest
    public void confettiRecipeCraftsTwelvePiecesFromPaperAndCmyDyes(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("confetti_alt"));
        CraftingInput grid = craftingGrid(2, 2,
                new ItemStack(net.minecraft.world.item.Items.PAPER),
                new ItemStack(net.minecraft.world.item.Items.CYAN_DYE),
                new ItemStack(net.minecraft.world.item.Items.MAGENTA_DYE),
                new ItemStack(net.minecraft.world.item.Items.YELLOW_DYE)
        );

        TestAsserts.assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected confetti recipe to match paper plus cyan/magenta/yellow dye");
        ItemStack result = recipe.assemble(grid);
        TestAsserts.assertTrue(helper, result.is(Items.CONFETTI), "Expected confetti recipe to produce confetti");
        TestAsserts.assertTrue(helper, result.getCount() == 12, "Expected confetti recipe to produce 12 confetti items");
        helper.succeed();
    }

    @GameTest
    public void confettiBallRecipeCraftsTwoBallsFromCrossOfConfettiAndGunpowder(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("confetti_ball"));
        ItemStack confetti = new ItemStack(Items.CONFETTI);
        CraftingInput grid = craftingGrid(3, 3,
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY,
                confetti.copy(), new ItemStack(net.minecraft.world.item.Items.GUNPOWDER), confetti.copy(),
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY
        );

        TestAsserts.assertTrue(helper, recipe.matches(grid, helper.getLevel()), "Expected confetti ball recipe to match its cross pattern");
        ItemStack result = recipe.assemble(grid);
        TestAsserts.assertTrue(helper, result.is(Items.CONFETTI_BALL), "Expected confetti ball recipe to produce confetti balls");
        TestAsserts.assertTrue(helper, result.getCount() == 2, "Expected confetti ball recipe to produce 2 confetti balls");
        helper.succeed();
    }

    @GameTest
    public void confettiBallRecipeRejectsMissingConfettiArm(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("confetti_ball"));
        ItemStack confetti = new ItemStack(Items.CONFETTI);
        CraftingInput grid = craftingGrid(3, 3,
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY,
                confetti.copy(), new ItemStack(net.minecraft.world.item.Items.GUNPOWDER), ItemStack.EMPTY,
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY
        );

        TestAsserts.assertFalse(helper, recipe.matches(grid, helper.getLevel()), "Expected confetti ball recipe to reject a pattern with a missing confetti arm");
        helper.succeed();
    }

    @GameTest
    public void confettiUseConsumesOneItemForSurvivalPlayers(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack stack = new ItemStack(Items.CONFETTI, 2);
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);

        InteractionResult result = Items.CONFETTI.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);

        TestAsserts.assertTrue(helper, result == InteractionResult.SUCCESS, "Expected confetti use to succeed");
        TestAsserts.assertTrue(helper, player.getMainHandItem().getCount() == 1, "Expected survival confetti use to consume one item");
        helper.succeed();
    }

    @GameTest
    public void confettiUseDoesNotConsumeItemForCreativePlayers(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.CREATIVE);
        ItemStack stack = new ItemStack(Items.CONFETTI, 2);
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);

        InteractionResult result = Items.CONFETTI.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);

        TestAsserts.assertTrue(helper, result == InteractionResult.SUCCESS, "Expected confetti use to succeed for creative players");
        TestAsserts.assertTrue(helper, player.getMainHandItem().getCount() == 2, "Expected creative confetti use not to consume the item");
        helper.succeed();
    }

    @GameTest
    public void confettiDispenseBehaviorConsumesOneItem(GameTestHelper helper) {
        BlockPos dispenserPos = new BlockPos(1, 2, 1);
        BlockPos absolutePos = helper.absolutePos(dispenserPos);
        BlockState state = net.minecraft.world.level.block.Blocks.DISPENSER.defaultBlockState()
                .setValue(DispenserBlock.FACING, Direction.NORTH);
        helper.getLevel().setBlockAndUpdate(absolutePos, state);

        DispenserBlockEntity dispenser = requireDispenser(helper, absolutePos);
        ItemStack stack = new ItemStack(Items.CONFETTI);
        dispenser.setItem(0, stack);

        ItemStack remaining = new Mod.ConfettiDispenseItemBehavior()
                .dispense(new BlockSource(helper.getLevel(), absolutePos, state, dispenser), stack);

        TestAsserts.assertTrue(helper, remaining.isEmpty() || remaining.getCount() == 0, "Expected confetti dispenser behavior to consume the loaded item");
        helper.succeed();
    }
}
