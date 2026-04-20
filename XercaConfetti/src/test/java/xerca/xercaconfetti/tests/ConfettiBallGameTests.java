package xerca.xercaconfetti.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.InteractionHand;
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
import xerca.xercaconfetti.Mod;
import xerca.xercaconfetti.entity.EntityConfettiBall;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public final class ConfettiBallGameTests {
    private static final String BASIC_TEMPLATE = "xercaconfetti:basic_test";
    private static final String CONFETTI_BATCH = "xercaconfetti_regressions";
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
        helper.assertTrue(!balls.isEmpty(), message);
        return balls.get(0);
    }

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
        for (ItemStack stack : stacks) {
            list.add(stack);
        }
        return CraftingInput.of(width, height, list);
    }

    private static DispenseItemBehavior getDispenseMethod(GameTestHelper helper, DispenserBlock dispenserBlock, ItemStack stack) {
        try {
            return (DispenseItemBehavior) DISPENSE_METHOD.invoke(dispenserBlock, helper.getLevel(), stack);
        } catch (ReflectiveOperationException e) {
            helper.assertTrue(false, "Failed to access dispenser behavior for confetti ball: " + e);
            return DispenseItemBehavior.NOOP;
        }
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CONFETTI_BATCH)
    public static void playerUseSpawnsConfettiBallAndConsumesOneItem(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        BlockPos playerPos = helper.absolutePos(new BlockPos(1, 2, 1));
        player.moveTo(Vec3.atCenterOf(playerPos));
        player.setYRot(180.0F);
        player.setXRot(0.0F);

        ItemStack stack = new ItemStack(Mod.CONFETTI_BALL, 2);
        player.setItemSlot(EquipmentSlot.MAINHAND, stack);

        Mod.CONFETTI_BALL.use(helper.getLevel(), player, InteractionHand.MAIN_HAND);

        helper.assertTrue(player.getMainHandItem().getCount() == 1, "Expected player throw to consume exactly one confetti ball");
        EntityConfettiBall ball = requireSingleBallNear(helper, new BlockPos(1, 2, 1), "Expected player use to spawn a confetti ball entity");
        helper.assertTrue(ball.getItem().is(Mod.CONFETTI_BALL), "Expected spawned confetti ball to keep the synced confetti ball item");
        helper.assertTrue(ball.getOwner() == player, "Expected thrown confetti ball to track the player as its owner");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CONFETTI_BATCH)
    public static void dispenserUseSpawnsConfettiBallWithoutSynchedDataCrash(GameTestHelper helper) {
        BlockPos dispenserPos = new BlockPos(1, 2, 1);
        BlockPos absolutePos = helper.absolutePos(dispenserPos);
        BlockState state = net.minecraft.world.level.block.Blocks.DISPENSER.defaultBlockState()
                .setValue(DispenserBlock.FACING, Direction.NORTH);
        helper.getLevel().setBlockAndUpdate(absolutePos, state);

        helper.assertTrue(helper.getLevel().getBlockEntity(absolutePos) instanceof DispenserBlockEntity,
                "Expected a dispenser block entity at the test position");
        DispenserBlockEntity dispenser = (DispenserBlockEntity) helper.getLevel().getBlockEntity(absolutePos);
        ItemStack stack = new ItemStack(Mod.CONFETTI_BALL);
        dispenser.setItem(0, stack);

        DispenseItemBehavior behavior = getDispenseMethod(helper, (DispenserBlock) state.getBlock(), stack);
        ItemStack remaining = behavior.dispense(new BlockSource(helper.getLevel(), absolutePos, state, dispenser), stack);

        helper.assertTrue(remaining.isEmpty() || remaining.getCount() == 0, "Expected dispenser launch to consume the loaded confetti ball");
        EntityConfettiBall ball = requireSingleBallNear(helper, dispenserPos.relative(Direction.NORTH), "Expected dispenser use to spawn a confetti ball entity");
        helper.assertTrue(ball.getItem().is(Mod.CONFETTI_BALL), "Expected dispenser-spawned confetti ball to have synced item data");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CONFETTI_BATCH)
    public static void confettiRecipeCraftsTwelvePiecesFromPaperAndCmyDyes(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("confetti"));
        CraftingInput grid = craftingGrid(2, 2,
                new ItemStack(net.minecraft.world.item.Items.PAPER),
                new ItemStack(net.minecraft.world.item.Items.CYAN_DYE),
                new ItemStack(net.minecraft.world.item.Items.MAGENTA_DYE),
                new ItemStack(net.minecraft.world.item.Items.YELLOW_DYE)
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected confetti recipe to match paper plus cyan/magenta/yellow dye");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Mod.CONFETTI), "Expected confetti recipe to produce confetti");
        helper.assertTrue(result.getCount() == 12, "Expected confetti recipe to produce 12 confetti items");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CONFETTI_BATCH)
    public static void confettiBallRecipeCraftsTwoBallsFromCrossOfConfettiAndGunpowder(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("confetti_ball"));
        ItemStack confetti = new ItemStack(Mod.CONFETTI);
        CraftingInput grid = craftingGrid(3, 3,
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY,
                confetti.copy(), new ItemStack(net.minecraft.world.item.Items.GUNPOWDER), confetti.copy(),
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY
        );

        helper.assertTrue(recipe.matches(grid, helper.getLevel()), "Expected confetti ball recipe to match its cross pattern");
        ItemStack result = recipe.assemble(grid, helper.getLevel().registryAccess());
        helper.assertTrue(result.is(Mod.CONFETTI_BALL), "Expected confetti ball recipe to produce confetti balls");
        helper.assertTrue(result.getCount() == 2, "Expected confetti ball recipe to produce 2 confetti balls");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = CONFETTI_BATCH)
    public static void confettiBallRecipeRejectsMissingConfettiArm(GameTestHelper helper) {
        CraftingRecipe recipe = requireCraftingRecipe(helper, recipeId("confetti_ball"));
        ItemStack confetti = new ItemStack(Mod.CONFETTI);
        CraftingInput grid = craftingGrid(3, 3,
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY,
                confetti.copy(), new ItemStack(net.minecraft.world.item.Items.GUNPOWDER), ItemStack.EMPTY,
                ItemStack.EMPTY, confetti.copy(), ItemStack.EMPTY
        );

        helper.assertFalse(recipe.matches(grid, helper.getLevel()), "Expected confetti ball recipe to reject a pattern with a missing confetti arm");
        helper.succeed();
    }
}
