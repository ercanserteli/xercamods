package xerca.xercatools.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.enchantment.ScytheEnchantments;
import xerca.xercatools.item.Items;

import java.util.List;

@SuppressWarnings("unused")
public class ScytheGameTests {

    private static BlockState maxAgeWheat() {
        CropBlock crop = (CropBlock) Blocks.WHEAT;
        return crop.defaultBlockState().setValue(CropBlock.AGE, crop.getMaxAge());
    }

    // ── Group D: crop harvesting ──────────────────────────────────────────────

    @GameTest
    public void scytheCanHarvestMaxAgeCrop(GameTestHelper helper) {
        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        float speed = Items.IRON_SCYTHE.getDestroySpeed(scythe, maxAgeWheat());
        TestAsserts.assertTrue(helper, speed > 0.0f, "Scythe should have destroy speed > 0 for max-age crop, got " + speed);
        helper.succeed();
    }

    @GameTest
    public void scytheCannotHarvestYoungCrop(GameTestHelper helper) {
        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        BlockState youngWheat = Blocks.WHEAT.defaultBlockState(); // age=0, not max
        float speed = Items.IRON_SCYTHE.getDestroySpeed(scythe, youngWheat);
        TestAsserts.assertTrue(helper, speed == 0.0f, "Scythe should have destroy speed 0 for non-max-age crop, got " + speed);
        helper.succeed();
    }

    @GameTest
    public void scytheMineBlockHarvestsAdjacentCropsWithSweepingEdge(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos north = center.north();
        BlockPos south = center.south();
        BlockPos east = center.east();
        BlockPos west = center.west();

        BlockState farmland = Blocks.FARMLAND.defaultBlockState();
        BlockState maxWheat = maxAgeWheat();
        for (BlockPos pos : List.of(center, north, south, east, west)) {
            helper.setBlock(pos.below(), farmland);
            helper.setBlock(pos, maxWheat);
        }

        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(reg.getOrThrow(Enchantments.SWEEPING_EDGE), 1);
        scythe.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        Items.IRON_SCYTHE.mineBlock(scythe, level, maxWheat, BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(center))), player);

        TestAsserts.assertTrue(helper,
                !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(north)))).is(Blocks.WHEAT) &&
                        !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(south)))).is(Blocks.WHEAT) &&
                        !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(east)))).is(Blocks.WHEAT) &&
                        !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(west)))).is(Blocks.WHEAT),
                "Sweeping Edge I should harvest all four cardinal neighbor crops");
        helper.succeed();
    }

    @GameTest
    public void scytheMineBlockSweepingLevel2HarvestsDiagonals(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        BlockPos center = new BlockPos(3, 2, 3);
        BlockPos ne = center.north().east();
        BlockPos nw = center.north().west();
        BlockPos se = center.south().east();
        BlockPos sw = center.south().west();

        BlockState farmland = Blocks.FARMLAND.defaultBlockState();
        BlockState maxWheat = maxAgeWheat();
        for (BlockPos pos : List.of(center, center.north(), center.south(), center.east(), center.west(), ne, nw, se, sw)) {
            helper.setBlock(pos.below(), farmland);
            helper.setBlock(pos, maxWheat);
        }

        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(reg.getOrThrow(Enchantments.SWEEPING_EDGE), 2);
        scythe.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        Items.IRON_SCYTHE.mineBlock(scythe, level, maxWheat, BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(center))), player);

        TestAsserts.assertTrue(helper,
                !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(ne)))).is(Blocks.WHEAT) &&
                        !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(nw)))).is(Blocks.WHEAT) &&
                        !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(se)))).is(Blocks.WHEAT) &&
                        !level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(sw)))).is(Blocks.WHEAT),
                "Sweeping Edge II should also harvest diagonal crops");
        helper.succeed();
    }

    @GameTest
    public void scytheWithoutSweepingEdgeDoesNotHarvestNeighbors(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        BlockPos center = new BlockPos(2, 2, 2);
        BlockPos north = center.north();
        BlockPos south = center.south();

        BlockState farmland = Blocks.FARMLAND.defaultBlockState();
        BlockState maxWheat = maxAgeWheat();
        for (BlockPos pos : List.of(center, north, south)) {
            helper.setBlock(pos.below(), farmland);
            helper.setBlock(pos, maxWheat);
        }

        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE); // no sweeping enchantment

        Items.IRON_SCYTHE.mineBlock(scythe, level, maxWheat, BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(center))), player);

        TestAsserts.assertTrue(helper,
                level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(north)))).is(Blocks.WHEAT) &&
                        level.getBlockState(BlockPos.containing(helper.absoluteVec(Vec3.atCenterOf(south)))).is(Blocks.WHEAT),
                "Scythe without Sweeping Edge must not harvest neighbors");
        helper.succeed();
    }

    // ── Group E: guillotine ───────────────────────────────────────────────────

    @GameTest
    public void guillotineRequiresFullPullToActivate(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);   // facing south (+Z)
        player.setXRot(0.0f);

        Pig pig = helper.spawn(EntityTypes.PIG, new BlockPos(2, 3, 3));
        pig.setNoAi(true);

        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(ScytheEnchantments.guillotineEnchantment(level.registryAccess()), 1);
        scythe.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, scythe);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        float initialHealth = pig.getHealth();
        // pull = 17/20 = 0.85 < 0.9 threshold → early return with no effect
        Items.IRON_SCYTHE.releaseUsing(scythe, level, player, 72000 - 17);

        TestAsserts.assertTrue(helper, pig.getHealth() == initialHealth,
                "Guillotine below 0.9 pull should not deal damage");
        helper.succeed();
    }

    @GameTest
    public void guillotineKillDropsHead(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);   // facing south (+Z)
        player.setXRot(0.0f);

        Pig pig = helper.spawn(EntityTypes.PIG, new BlockPos(2, 3, 3));
        pig.setNoAi(true);
        pig.setHealth(1.0f); // near-dead so the strike kills it

        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(ScytheEnchantments.guillotineEnchantment(level.registryAccess()), 1);
        scythe.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, scythe);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        Vec3 pigPos = pig.position();
        // pull = 19/20 = 0.95 >= 0.9 → guillotine activates
        Items.IRON_SCYTHE.releaseUsing(scythe, level, player, 72000 - 19);

        TestAsserts.assertTrue(helper, pig.isDeadOrDying(), "Guillotine at ≥0.9 pull should kill the pig");

        AABB searchBox = new AABB(pigPos.x - 3, pigPos.y - 3, pigPos.z - 3,
                pigPos.x + 3, pigPos.y + 3, pigPos.z + 3);
        boolean headDropped = !level.getEntitiesOfClass(ItemEntity.class, searchBox).isEmpty();
        TestAsserts.assertTrue(helper, headDropped, "Guillotine kill should drop a head item entity");
        helper.succeed();
    }
}
