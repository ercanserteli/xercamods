package xerca.xercatools.tests;

import net.fabricmc.fabric.api.gametest.v1.GameTest;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityTypes;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.entity.EntityGrabHook;
import xerca.xercatools.item.Items;

@SuppressWarnings("unused")
public class GrabHookGameTests {
    private static final double DEFAULT_SPEED = 1.5;

    // Positions a player facing south at relative (2.5, 2.0, 2.5).
    private static Player makeSouthPlayer(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);  // facing south (+Z)
        player.setXRot(0.0f);
        return player;
    }

    // ── speed on launch ───────────────────────────────────────────────────────

    @GameTest
    public void grabHookLaunchesWithDefaultSpeed(GameTestHelper helper) {
        Player player = makeSouthPlayer(helper);
        EntityGrabHook hook = new EntityGrabHook(helper.getLevel(), player, new ItemStack(Items.GRAB_HOOK), 1.0f);

        double speed = hook.getDeltaMovement().length();
        TestAsserts.assertTrue(helper, Math.abs(speed - DEFAULT_SPEED) < 0.01,
                "Hook default launch speed should be " + DEFAULT_SPEED + ", got " + speed);
        helper.succeed();
    }

    @GameTest
    public void grabHookTurboGrabIncreasesLaunchSpeed(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);

        ItemStack rod = new ItemStack(Items.GRAB_HOOK);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(GrabHookEnchantments.turboGrab(level.registryAccess()), 1);
        rod.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        EntityGrabHook hook = new EntityGrabHook(level, player, rod, 1.0f);
        double expectedSpeed = DEFAULT_SPEED * 1.25;  // 1 + turbo*0.25
        double speed = hook.getDeltaMovement().length();
        TestAsserts.assertTrue(helper, Math.abs(speed - expectedSpeed) < 0.01,
                "Turbo Grab I launch speed should be " + expectedSpeed + ", got " + speed);
        helper.succeed();
    }

    @GameTest
    public void grabHookPullAmountScalesSpeed(GameTestHelper helper) {
        Player player = makeSouthPlayer(helper);

        EntityGrabHook hook = new EntityGrabHook(helper.getLevel(), player, new ItemStack(Items.GRAB_HOOK), 0.5f);
        double expectedSpeed = DEFAULT_SPEED * 0.5;
        double speed = hook.getDeltaMovement().length();
        TestAsserts.assertTrue(helper, Math.abs(speed - expectedSpeed) < 0.01,
                "Pull amount 0.5 should give half speed, got " + speed);
        helper.succeed();
    }

    // ── retracting after 20 ticks in air ─────────────────────────────────────

    @GameTest(maxTicks = 60)
    public void grabHookStartsRetractingAfterAirTime(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);

        EntityGrabHook hook = new EntityGrabHook(level, player, new ItemStack(Items.GRAB_HOOK), 1.0f);
        // Position hook high above the test structure so it stays in air for 20 ticks.
        Vec3 highPos = helper.absoluteVec(new Vec3(2.5, 30.0, 2.5));
        hook.setPos(highPos.x, highPos.y, highPos.z);
        hook.setDeltaMovement(0, DEFAULT_SPEED, 0); // fly straight up, no block collisions
        level.addFreshEntity(hook);

        // ticksInAir reaches 20 on the 20th server tick; check after 22 ticks.
        helper.runAtTickTime(helper.getTick() + 22, () -> {
            TestAsserts.assertTrue(helper, hook.isReturning(),
                    "Hook should be returning after 20 ticks in air");
            helper.succeed();
        });
    }

    // ── catching entities ─────────────────────────────────────────────────────

    @GameTest
    public void grabHookCatchesEntityOnPath(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);

        // Pig at relative (2, 3, 3) → center at (abs+2.5, abs+3.0, abs+3.5).
        // Hook eye y = abs+2.0+1.62 = abs+3.62, inside pig box y=[abs+3, abs+3.9] ✓
        Pig pig = helper.spawn(EntityTypes.PIG, new BlockPos(2, 3, 3));
        pig.setNoAi(true);

        ItemStack rod = new ItemStack(Items.GRAB_HOOK);
        EntityGrabHook hook = new EntityGrabHook(level, player, rod, 1.0f);
        // Hook launches from player eye going south; one tick covers 1.5 blocks in +Z.
        level.addFreshEntity(hook);

        // After one tick the ray sweeps past the pig (pig z-start ≈ abs+3.05, ray to ≈ abs+4.0).
        hook.tick();

        TestAsserts.assertTrue(helper, hook.getCaughtEntity() == pig,
                "Hook should catch the pig on first tick");
        helper.succeed();
    }

    @GameTest
    public void grabHookDamagesEntityByDefault(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);
        Pig pig = helper.spawn(EntityTypes.PIG, new BlockPos(2, 3, 3));
        pig.setNoAi(true);

        EntityGrabHook hook = new EntityGrabHook(level, player, new ItemStack(Items.GRAB_HOOK), 1.0f);
        level.addFreshEntity(hook);

        float healthBefore = pig.getHealth();
        hook.tick();

        TestAsserts.assertTrue(helper, pig.getHealth() < healthBefore,
                "Hook without Gentle Grab should deal 3 damage on catch");
        helper.succeed();
    }

    @GameTest
    public void grabHookKeepsCorpseWhenEntityDiesOnImpact(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);
        Pig pig = helper.spawn(EntityTypes.PIG, new BlockPos(2, 3, 3));
        pig.setNoAi(true);
        pig.setHealth(1.0f); // 3 damage on impact will kill it

        EntityGrabHook hook = new EntityGrabHook(level, player, new ItemStack(Items.GRAB_HOOK), 1.0f);
        level.addFreshEntity(hook);

        hook.tick();

        TestAsserts.assertTrue(helper, !pig.isAlive(), "Pig should be killed by the impact damage");
        TestAsserts.assertTrue(helper, !pig.isRemoved(), "Corpse should still exist right after death");
        TestAsserts.assertTrue(helper, hook.getCaughtEntity() == pig,
                "Hook should keep the corpse caught instead of dropping it");
        TestAsserts.assertTrue(helper, !hook.isRemoved(),
                "Hook should not be discarded when the catch dies on impact");
        helper.succeed();
    }

    @GameTest
    public void grabHookGentleGrabDealsNoDamage(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);
        Pig pig = helper.spawn(EntityTypes.PIG, new BlockPos(2, 3, 3));
        pig.setNoAi(true);

        ItemStack rod = new ItemStack(Items.GRAB_HOOK);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(GrabHookEnchantments.gentleGrab(level.registryAccess()), 1);
        rod.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        EntityGrabHook hook = new EntityGrabHook(level, player, rod, 1.0f);
        level.addFreshEntity(hook);

        float healthBefore = pig.getHealth();
        hook.tick();

        TestAsserts.assertTrue(helper, hook.getCaughtEntity() == pig,
                "Gentle Grab should still catch the entity");
        TestAsserts.assertTrue(helper, pig.getHealth() == healthBefore,
                "Gentle Grab should deal no damage on catch");
        helper.succeed();
    }
}
