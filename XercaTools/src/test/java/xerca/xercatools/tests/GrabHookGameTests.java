package xerca.xercatools.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.enchantment.GrabHookEnchantments;
import xerca.xercatools.entity.EntityGrabHook;
import xerca.xercatools.item.Items;

public class GrabHookGameTests {
    private static final String BASIC_TEMPLATE = "xercatools:basic_test";
    private static final String WEAPONS_BATCH = "xercatools_tests";
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

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void grabHookLaunchesWithDefaultSpeed(GameTestHelper helper) {
        Player player = makeSouthPlayer(helper);
        EntityGrabHook hook = new EntityGrabHook(helper.getLevel(), player, new ItemStack(Items.GRAB_HOOK), 1.0f);

        double speed = hook.getDeltaMovement().length();
        helper.assertTrue(Math.abs(speed - DEFAULT_SPEED) < 0.01,
            "Hook default launch speed should be " + DEFAULT_SPEED + ", got " + speed);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void grabHookTurboGrabIncreasesLaunchSpeed(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);

        ItemStack rod = new ItemStack(Items.GRAB_HOOK);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(GrabHookEnchantments.turboGrab(level.registryAccess()), 1);
        rod.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        EntityGrabHook hook = new EntityGrabHook(level, player, rod, 1.0f);
        double expectedSpeed = DEFAULT_SPEED * 1.25;  // 1 + turbo*0.25
        double speed = hook.getDeltaMovement().length();
        helper.assertTrue(Math.abs(speed - expectedSpeed) < 0.01,
            "Turbo Grab I launch speed should be " + expectedSpeed + ", got " + speed);
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void grabHookPullAmountScalesSpeed(GameTestHelper helper) {
        Player player = makeSouthPlayer(helper);

        EntityGrabHook hook = new EntityGrabHook(helper.getLevel(), player, new ItemStack(Items.GRAB_HOOK), 0.5f);
        double expectedSpeed = DEFAULT_SPEED * 0.5;
        double speed = hook.getDeltaMovement().length();
        helper.assertTrue(Math.abs(speed - expectedSpeed) < 0.01,
            "Pull amount 0.5 should give half speed, got " + speed);
        helper.succeed();
    }

    // ── retracting after 20 ticks in air ─────────────────────────────────────

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void grabHookStartsRetractingAfterAirTime(GameTestHelper helper) {
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
            helper.assertTrue(hook.isReturning(),
                "Hook should be returning after 20 ticks in air");
            helper.succeed();
        });
    }

    // ── catching entities ─────────────────────────────────────────────────────

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void grabHookCatchesEntityOnPath(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);

        // Pig at relative (2, 3, 3) → center at (abs+2.5, abs+3.0, abs+3.5).
        // Hook eye y = abs+2.0+1.62 = abs+3.62, inside pig box y=[abs+3, abs+3.9] ✓
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(2, 3, 3));

        ItemStack rod = new ItemStack(Items.GRAB_HOOK);
        EntityGrabHook hook = new EntityGrabHook(level, player, rod, 1.0f);
        // Hook launches from player eye going south; one tick covers 1.5 blocks in +Z.
        level.addFreshEntity(hook);

        // After one tick the ray sweeps past the pig (pig z-start ≈ abs+3.05, ray to ≈ abs+4.0).
        hook.tick();

        helper.assertTrue(hook.getCaughtEntity() == pig,
            "Hook should catch the pig on first tick");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void grabHookDamagesEntityByDefault(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(2, 3, 3));

        EntityGrabHook hook = new EntityGrabHook(level, player, new ItemStack(Items.GRAB_HOOK), 1.0f);
        level.addFreshEntity(hook);

        float healthBefore = pig.getHealth();
        hook.tick();

        helper.assertTrue(pig.getHealth() < healthBefore,
            "Hook without Gentle Grab should deal 3 damage on catch");
        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void grabHookGentleGrabDealsNoDamage(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = makeSouthPlayer(helper);
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(2, 3, 3));

        ItemStack rod = new ItemStack(Items.GRAB_HOOK);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(GrabHookEnchantments.gentleGrab(level.registryAccess()), 1);
        rod.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        EntityGrabHook hook = new EntityGrabHook(level, player, rod, 1.0f);
        level.addFreshEntity(hook);

        float healthBefore = pig.getHealth();
        hook.tick();

        helper.assertTrue(hook.getCaughtEntity() == pig,
            "Gentle Grab should still catch the entity");
        helper.assertTrue(pig.getHealth() == healthBefore,
            "Gentle Grab should deal no damage on catch");
        helper.succeed();
    }
}
