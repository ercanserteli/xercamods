package xerca.xercapaint.tests;

import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityEasel;
import xerca.xercapaint.item.Items;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("PMD.AvoidAccessibilityAlteration")
public class EaselTests {
    private static final String BASIC_TEMPLATE = "xercapaint:basic_test";
    private static final Field PAINTER_FIELD;
    private static final Field DROP_DEFERRED_FIELD;

    static {
        try {
            PAINTER_FIELD = EntityEasel.class.getDeclaredField("painter");
            PAINTER_FIELD.setAccessible(true);
            DROP_DEFERRED_FIELD = EntityEasel.class.getDeclaredField("dropDeferred");
            DROP_DEFERRED_FIELD.setAccessible(true);
        } catch (ReflectiveOperationException e) {
            throw new ExceptionInInitializerError(e);
        }
    }

    private static EntityEasel requireSingleEaselNear(GameTestHelper helper, BlockPos relativePos, String failureMessage) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(1.5D, 2.0D, 1.5D);
        List<? extends Entity> list = helper.getLevel().getEntities(Entities.EASEL, searchBox, Entity::isAlive);
        helper.assertTrue(!list.isEmpty(), failureMessage);
        return (EntityEasel) list.getFirst();
    }

    private static long countItemDropsNear(GameTestHelper helper, BlockPos relativePos, Item item) {
        AABB searchBox = new AABB(helper.absolutePos(relativePos)).inflate(2.5D, 2.5D, 2.5D);
        return helper.getLevel().getEntitiesOfClass(ItemEntity.class, searchBox, it -> it.getItem().is(item)).size();
    }

    private static Player getPainter(GameTestHelper helper, EntityEasel easel) {
        try {
            return (Player) PAINTER_FIELD.get(easel);
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to read easel painter field: " + e);
            throw new IllegalStateException("Unreachable after GameTest assertion failure", e);
        }
    }

    private static Runnable getDeferredDrop(GameTestHelper helper, EntityEasel easel) {
        try {
            return (Runnable) DROP_DEFERRED_FIELD.get(easel);
        } catch (IllegalAccessException e) {
            helper.assertTrue(false, "Failed to read easel deferred-drop field: " + e);
            throw new IllegalStateException("Unreachable after GameTest assertion failure", e);
        }
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void placingEaselFacesPlayerFromAllEightDirections(GameTestHelper helper) {
        final BlockPos easelLand = new BlockPos(3, 1, 2);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absEaselLand = helper.absolutePos(easelLand);
        Vec3 target = Vec3.atCenterOf(absEaselLand);
        helper.setBlock(easelLand, Blocks.STONE);

        int[][] offsets = {
                {0, -1},  // north
                {1, -1},  // north-east
                {1, 0},   // east
                {1, 1},   // south-east
                {0, 1},   // south
                {-1, 1},  // south-west
                {-1, 0},  // west
                {-1, -1}  // north-west
        };

        for (int i = 0; i < offsets.length; i++) {
            int[] offset = offsets[i];
            player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_EASEL, 1));
            player.moveTo(target.x + offset[0], absEaselLand.getY(), target.z + offset[1], 0.0F, 0.0F);
            player.lookAt(EntityAnchorArgument.Anchor.EYES, target);
            float expectedYaw = (float) Mth.floor((Mth.wrapDegrees(player.getYRot() - 180.0F) + 22.5F) / 45.0F) * 45.0F;

            helper.placeAt(player, player.getMainHandItem(), easelLand, Direction.UP);
            EntityEasel easel = requireSingleEaselNear(helper, easelLand, "Missing easel after placement in case " + i);

            float actualYaw = Mth.wrapDegrees(easel.getYRot());
            float expected = Mth.wrapDegrees(expectedYaw);
            helper.assertTrue(Math.abs(Mth.wrapDegrees(actualYaw - expected)) < 0.001F,
                    "Easel yaw mismatch for case " + i + " (actual=" + actualYaw + ", expected=" + expected + ")");
            easel.kill();
        }

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void rightClickWithCanvasEmptyHandAndPaletteHasExpectedModes(GameTestHelper helper) {
        final BlockPos easelLand = new BlockPos(3, 1, 2);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absEaselLand = helper.absolutePos(easelLand);

        helper.setBlock(easelLand, Blocks.STONE);
        player.moveTo(Vec3.atBottomCenterOf(absEaselLand).add(1.0D, 0.0D, 0.0D));
        player.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(absEaselLand));

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_EASEL, 1));
        helper.placeAt(player, player.getMainHandItem(), easelLand, Direction.UP);
        EntityEasel easel = requireSingleEaselNear(helper, easelLand, "No easel was spawned near placement area");

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_CANVAS, 1));
        InteractionResult putCanvasResult = player.interactOn(easel, InteractionHand.MAIN_HAND);
        helper.assertTrue(putCanvasResult.consumesAction(), "Expected right-click with canvas to consume interaction");
        helper.assertTrue(easel.getItem().is(Items.ITEM_CANVAS), "Expected canvas to be inserted into easel");
        helper.assertTrue(player.getMainHandItem().isEmpty(), "Expected canvas stack to shrink to empty after insertion");

        player.setItemSlot(EquipmentSlot.MAINHAND, ItemStack.EMPTY);
        InteractionResult viewResult = player.interactOn(easel, InteractionHand.MAIN_HAND);
        helper.assertTrue(viewResult.consumesAction(), "Expected empty-hand interaction to consume interaction");
        helper.assertTrue(getPainter(helper, easel) == null, "Expected empty-hand interaction to stay in view mode (no editor lock)");

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_PALETTE, 1));
        InteractionResult editResult = player.interactOn(easel, InteractionHand.MAIN_HAND);
        helper.assertTrue(editResult.consumesAction(), "Expected palette interaction to consume interaction");
        helper.assertTrue(Objects.equals(getPainter(helper, easel), player), "Expected palette interaction to acquire easel editor lock");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void secondPlayerCannotStealEditLockAndCanBreakAndDropBothItems(GameTestHelper helper) {
        final BlockPos easelLand = new BlockPos(3, 1, 2);
        BlockPos absEaselLand = helper.absolutePos(easelLand);
        Vec3 target = Vec3.atCenterOf(absEaselLand);

        Player firstPlayer = helper.makeMockPlayer(GameType.SURVIVAL);
        Player secondPlayer = helper.makeMockPlayer(GameType.SURVIVAL);

        helper.setBlock(easelLand, Blocks.STONE);

        firstPlayer.moveTo(target.x + 1.0D, absEaselLand.getY(), target.z, 0.0F, 0.0F);
        firstPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, target);
        firstPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_EASEL, 1));
        helper.placeAt(firstPlayer, firstPlayer.getMainHandItem(), easelLand, Direction.UP);
        EntityEasel easel = requireSingleEaselNear(helper, easelLand, "No easel was spawned for two-player test");

        firstPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_CANVAS, 1));
        firstPlayer.interactOn(easel, InteractionHand.MAIN_HAND);
        helper.assertTrue(easel.getItem().is(Items.ITEM_CANVAS), "Expected canvas on easel before edit-lock checks");

        firstPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_PALETTE, 1));
        firstPlayer.interactOn(easel, InteractionHand.MAIN_HAND);
        helper.assertTrue(Objects.equals(getPainter(helper, easel), firstPlayer), "First player should hold the edit lock");

        secondPlayer.moveTo(target.x - 1.0D, absEaselLand.getY(), target.z, 0.0F, 0.0F);
        secondPlayer.lookAt(EntityAnchorArgument.Anchor.EYES, target);
        secondPlayer.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_PALETTE, 1));
        secondPlayer.interactOn(easel, InteractionHand.MAIN_HAND);
        helper.assertTrue(Objects.equals(getPainter(helper, easel), firstPlayer), "Second player should not replace first player as editor");

        DamageSource secondAttack = helper.getLevel().damageSources().playerAttack(secondPlayer);
        easel.hurt(secondAttack, 1.0F);
        helper.assertTrue(getDeferredDrop(helper, easel) != null, "Expected deferred drop while first player editor is still active");

        // In GameTests we don't have a real client ack; clear painter to emulate GUI close ack
        easel.setPainter(null);
        easel.tick();

        helper.assertTrue(easel.getItem().isEmpty(), "Expected canvas to be removed from easel after deferred drop runs");
        helper.assertTrue(countItemDropsNear(helper, easelLand, Items.ITEM_CANVAS) >= 1,
                "Expected canvas item drop after break while another player was editing");

        easel.hurt(secondAttack, 1.0F);
        helper.assertTrue(!easel.isAlive() || easel.isRemoved(), "Expected easel entity to be removed after second break");
        helper.assertTrue(countItemDropsNear(helper, easelLand, Items.ITEM_EASEL) >= 1,
                "Expected easel item drop when broken by second player");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void easelCanBeBrokenByExplosion(GameTestHelper helper) {
        final BlockPos easelLand = new BlockPos(3, 1, 2);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absEaselLand = helper.absolutePos(easelLand);

        helper.setBlock(easelLand, Blocks.STONE);
        player.moveTo(Vec3.atBottomCenterOf(absEaselLand).add(1.0D, 0.0D, 0.0D));
        player.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(absEaselLand));

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_EASEL, 1));
        helper.placeAt(player, player.getMainHandItem(), easelLand, Direction.UP);
        EntityEasel easel = requireSingleEaselNear(helper, easelLand, "No easel was spawned for explosion test");

        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_CANVAS, 1));
        player.interactOn(easel, InteractionHand.MAIN_HAND);
        helper.assertTrue(easel.getItem().is(Items.ITEM_CANVAS), "Expected canvas to be mounted before explosion break test");

        DamageSource explosion = helper.getLevel().damageSources().explosion(null, null);
        easel.hurt(explosion, 1.0F);

        helper.assertTrue(easel.isRemoved(), "Expected easel entity to be removed by explosion damage");
        helper.assertTrue(countItemDropsNear(helper, easelLand, Items.ITEM_EASEL) >= 1,
                "Expected easel item to drop when broken by explosion");
        helper.assertTrue(countItemDropsNear(helper, easelLand, Items.ITEM_CANVAS) >= 1,
                "Expected mounted canvas to drop when broken by explosion");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE)
    public static void invulnerableTaggedEaselIgnoresPlayerAndExplosionDamage(GameTestHelper helper) {
        final BlockPos easelLand = new BlockPos(3, 1, 2);
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        BlockPos absEaselLand = helper.absolutePos(easelLand);

        helper.setBlock(easelLand, Blocks.STONE);
        player.moveTo(Vec3.atBottomCenterOf(absEaselLand).add(1.0D, 0.0D, 0.0D));
        player.lookAt(EntityAnchorArgument.Anchor.EYES, Vec3.atCenterOf(absEaselLand));

        ItemStack invulnerableEaselStack = new ItemStack(Items.ITEM_EASEL, 1);
        CompoundTag entityTag = new CompoundTag();
        entityTag.putBoolean("Invulnerable", true);
        invulnerableEaselStack.set(DataComponents.ENTITY_DATA, CustomData.of(entityTag));
        player.setItemSlot(EquipmentSlot.MAINHAND, invulnerableEaselStack);

        helper.placeAt(player, player.getMainHandItem(), easelLand, Direction.UP);
        EntityEasel easel = requireSingleEaselNear(helper, easelLand, "No easel was spawned for invulnerable-tag test");

        helper.assertTrue(easel.isInvulnerable(), "Expected easel with Invulnerable:1b to be invulnerable");
        helper.assertTrue(countItemDropsNear(helper, easelLand, Items.ITEM_EASEL) == 0,
                "Expected no easel drops before invulnerability damage checks");

        DamageSource playerDamage = helper.getLevel().damageSources().playerAttack(player);
        DamageSource explosionDamage = helper.getLevel().damageSources().explosion(null, null);
        easel.hurt(playerDamage, 1.0F);
        easel.hurt(explosionDamage, 1.0F);

        helper.assertTrue(easel.isAlive() && !easel.isRemoved(), "Invulnerable easel should remain after player/explosion damage");
        helper.assertTrue(countItemDropsNear(helper, easelLand, Items.ITEM_EASEL) == 0,
                "Invulnerable easel should not drop itself on damage");
        helper.assertTrue(countItemDropsNear(helper, easelLand, Items.ITEM_CANVAS) == 0,
                "Invulnerable easel should not drop canvas on damage");

        helper.succeed();
    }
}
