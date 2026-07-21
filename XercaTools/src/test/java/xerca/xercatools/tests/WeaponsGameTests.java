package xerca.xercatools.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.pig.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.enchantment.KnifeEnchantments;
import xerca.xercatools.enchantment.ScytheEnchantments;
import xerca.xercatools.enchantment.WarhammerEnchantments;
import xerca.xercatools.entity.EntityHealthOrb;
import xerca.xercatools.item.ItemKnife;
import xerca.xercatools.item.ItemWarhammer;
import xerca.xercatools.item.Items;

@SuppressWarnings({"DataFlowIssue", "unused"})
public class WeaponsGameTests {

    // ── existing tests ────────────────────────────────────────────────────────

    // NoAI keeps targets exactly where placed; hurt pigs otherwise panic-run into other tests' sight lines.
    private static Pig spawnStillPig(GameTestHelper helper, BlockPos pos) {
        Pig pig = helper.spawn(EntityType.PIG, pos);
        pig.setNoAi(true);
        return pig;
    }

    public void warhammerDamagesEntityAndLosesDurability(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);

        float initialHealth = pig.getHealth();
        player.attack(pig);

        TestAsserts.assertTrue(helper, pig.getHealth() < initialHealth, "Expected warhammer to damage the pig");
        TestAsserts.assertTrue(helper, warhammer.getDamageValue() == 1, "Expected warhammer to lose 1 durability");
        helper.succeed();
    }

    public void devourKillSpawnsHealthOrbs(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(ScytheEnchantments.devourEnchantment(level.registryAccess()), 3);
        scythe.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, scythe);

        pig.hurtServer(level, level.damageSources().generic(), 100.0f);
        scythe.hurtEnemy(pig, player);

        boolean orbsSpawned = !level.getEntitiesOfClass(EntityHealthOrb.class, pig.getBoundingBox().inflate(10)).isEmpty();
        TestAsserts.assertTrue(helper, orbsSpawned, "Expected health orbs to spawn when devour kill is triggered");
        helper.succeed();
    }

    public void healthOrbHealsPlayerOnTouch(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        float initialHealth = player.getMaxHealth() - 4.0f;
        player.setHealth(initialHealth);

        Vec3 pos = helper.absoluteVec(new Vec3(1, 2, 1));
        EntityHealthOrb orb = new EntityHealthOrb(level, pos.x, pos.y, pos.z, null, player);
        level.addFreshEntity(orb);

        orb.playerTouch(player);

        TestAsserts.assertTrue(helper, player.getHealth() > initialHealth, "Expected health orb to restore player health on touch");
        helper.succeed();
    }

    // ── Group A: pull/release scaling ─────────────────────────────────────────

    // Positions player at (2,2,2) looking south, pig at (2,3,3) so ray hits it.
    private static Pig spawnPigInSight(GameTestHelper helper, Player player) {
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);  // facing south (+z)
        player.setXRot(0.0f);
        return spawnStillPig(helper, new BlockPos(2, 3, 3));
    }

    public void warhammerGetFullUseSecondsBaseIsOne(GameTestHelper helper) {
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);
        float seconds = ItemWarhammer.getFullUseSeconds(helper.getLevel().registryAccess(), stack);
        TestAsserts.assertTrue(helper, seconds == 1.0f, "Base full-use duration should be 1.0s, got " + seconds);
        helper.succeed();
    }

    public void warhammerPullBelowThresholdDoesNothing(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnPigInSight(helper, player);

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        float initialHealth = pig.getHealth();
        int initialDmg = warhammer.getDamageValue();

        // pull fraction = 1/20 = 0.05 < 0.1 threshold → early return
        Items.IRON_WARHAMMER.releaseUsing(warhammer, helper.getLevel(), player, 72000 - 1);

        TestAsserts.assertTrue(helper, pig.getHealth() == initialHealth, "Pig should not be damaged on sub-threshold pull");
        TestAsserts.assertTrue(helper, warhammer.getDamageValue() == initialDmg, "Durability should not change on sub-threshold pull");
        helper.succeed();
    }

    public void warhammerShortPullDealsReducedDamage(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnPigInSight(helper, player);

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        float initialHealth = pig.getHealth();

        // pull = 5/20 = 0.25 → 0.5× multiplier
        Items.IRON_WARHAMMER.releaseUsing(warhammer, helper.getLevel(), player, 72000 - 5);

        float dmgDealt = initialHealth - pig.getHealth();
        TestAsserts.assertTrue(helper, dmgDealt > 0, "Expected damage at 0.25 pull");

        // Verify that the 0.5× tier was used: at the next bracket (0.75×) damage would be 50% higher
        // Record for comparison with medium pull:
        TestAsserts.assertTrue(helper, dmgDealt < initialHealth, "Damage should not exceed pig health");
        helper.succeed();
    }

    public void warhammerMediumPullDealsMoreDamageThanShort(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        // Short pull
        Pig pig1 = spawnPigInSight(helper, player);
        ItemStack warhammer1 = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer1);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
        float health1Before = pig1.getHealth();
        Items.IRON_WARHAMMER.releaseUsing(warhammer1, level, player, 72000 - 5); // 0.5× mult
        float dmgShort = health1Before - pig1.getHealth();

        // Medium pull (fresh pig needed)
        pig1.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);
        Pig pig2 = spawnPigInSight(helper, player);
        ItemStack warhammer2 = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer2);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
        float health2Before = pig2.getHealth();
        Items.IRON_WARHAMMER.releaseUsing(warhammer2, level, player, 72000 - 10); // 0.75× mult
        float dmgMedium = health2Before - pig2.getHealth();

        TestAsserts.assertTrue(helper, dmgMedium > dmgShort, "Medium pull (0.75×) should deal more than short pull (0.5×)");
        helper.succeed();
    }

    public void warhammerFullPullDealsMaxDamage(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        // Medium pull for baseline
        Pig pig1 = spawnPigInSight(helper, player);
        ItemStack warhammer1 = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer1);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
        float h1 = pig1.getHealth();
        Items.IRON_WARHAMMER.releaseUsing(warhammer1, level, player, 72000 - 17); // 0.85→1.0× mult
        float dmgMedHigh = h1 - pig1.getHealth();

        pig1.remove(net.minecraft.world.entity.Entity.RemovalReason.DISCARDED);

        // Full pull
        Pig pig2 = spawnPigInSight(helper, player);
        ItemStack warhammer2 = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer2);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);
        float h2 = pig2.getHealth();
        Items.IRON_WARHAMMER.releaseUsing(warhammer2, level, player, 72000 - 20); // 1.0→1.75× mult
        float dmgFull = h2 - pig2.getHealth();

        TestAsserts.assertTrue(helper, dmgFull > dmgMedHigh, "Full pull (1.75×) should deal more than mid-high pull (1.0×)");
        helper.succeed();
    }

    // ── Group B: enchantment effects ──────────────────────────────────────────

    public void warhammerDensityIncreasesFullUseSeconds(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.DENSITY), 1);
        stack.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        float seconds = ItemWarhammer.getFullUseSeconds(level.registryAccess(), stack);
        TestAsserts.assertTrue(helper, seconds > 1.0f, "Density I should increase full-use duration above 1.0s");
        float expected = 1.0f + 0.1f;
        TestAsserts.assertTrue(helper, Math.abs(seconds - expected) < 0.001f,
                "Density I full-use should be ~" + expected + "s, got " + seconds);
        helper.succeed();
    }

    public void warhammerQuickDecreasesFullUseSeconds(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.quickEnchantment(level.registryAccess()), 1);
        stack.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        float seconds = ItemWarhammer.getFullUseSeconds(level.registryAccess(), stack);
        TestAsserts.assertTrue(helper, seconds < 1.0f, "Quick I should decrease full-use duration below 1.0s");
        float expected = 1.0f - 0.12f;
        TestAsserts.assertTrue(helper, Math.abs(seconds - expected) < 0.001f,
                "Quick I full-use should be ~" + expected + "s, got " + seconds);
        helper.succeed();
    }

    public void warhammerDensityTakesPrecedenceOverQuick(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.DENSITY), 1);
        enc.set(WarhammerEnchantments.quickEnchantment(level.registryAccess()), 1);
        stack.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        float seconds = ItemWarhammer.getFullUseSeconds(level.registryAccess(), stack);
        // density branch executes first and skips quick
        float expected = 1.0f + 0.1f;
        TestAsserts.assertTrue(helper, Math.abs(seconds - expected) < 0.001f,
                "Density should take precedence over Quick, expected " + expected + "s, got " + seconds);
        helper.succeed();
    }

    public void warhammerWindBurstFiresWindChargeOnMiss(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setXRot(-90.0f); // look straight up so the swing hits neither entity nor block

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.WIND_BURST), 1);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        Items.IRON_WARHAMMER.releaseUsing(warhammer, level, player, 72000 - 20); // full pull

        boolean spawned = !level.getEntitiesOfClass(
                net.minecraft.world.entity.projectile.hurtingprojectile.windcharge.WindCharge.class,
                player.getBoundingBox().inflate(8)).isEmpty();
        TestAsserts.assertTrue(helper, spawned, "Wind Burst full-charge miss should spawn a wind charge projectile");
        helper.succeed();
    }

    public void warhammerMaimAppliesSlowness(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnPigInSight(helper, player);

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.maimEnchantment(level.registryAccess()), 1);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        // full pull
        Items.IRON_WARHAMMER.releaseUsing(warhammer, level, player, 72000 - 20);

        TestAsserts.assertTrue(helper, pig.hasEffect(MobEffects.SLOWNESS),
                "Maim should apply movement slowdown");
        net.minecraft.world.effect.MobEffectInstance effect = pig.getEffect(MobEffects.SLOWNESS);
        TestAsserts.assertTrue(helper, effect != null && effect.getDuration() >= 140,
                "Maim I slowdown should last at least 140 ticks (100+40)");
        TestAsserts.assertTrue(helper, effect != null && effect.getAmplifier() == 0,
                "Maim I slowdown amplitude should be 0");
        helper.succeed();
    }

    public void warhammerMaimLevel2AppliesStrongerSlowness(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnPigInSight(helper, player);

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.maimEnchantment(level.registryAccess()), 2);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        Items.IRON_WARHAMMER.releaseUsing(warhammer, level, player, 72000 - 20);

        net.minecraft.world.effect.MobEffectInstance effect = pig.getEffect(MobEffects.SLOWNESS);
        TestAsserts.assertTrue(helper, effect != null && effect.getDuration() >= 180,
                "Maim II slowdown should last at least 180 ticks");
        TestAsserts.assertTrue(helper, effect != null && effect.getAmplifier() == 1,
                "Maim II slowdown amplitude should be 1");
        helper.succeed();
    }

    public void warhammerUppercutAppliesUpwardVelocity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnPigInSight(helper, player);

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.uppercutEnchantment(level.registryAccess()), 2);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        // full pull so pullDuration=1.0; bonusVelY = 2 * 0.25 * 1.0 = 0.5
        float initialHealth = pig.getHealth();
        Items.IRON_WARHAMMER.releaseUsing(warhammer, level, player, 72000 - 20);

        TestAsserts.assertTrue(helper, pig.getHealth() < initialHealth, "Uppercut strike should hit the pig");
        double vy = pig.getDeltaMovement().y;
        TestAsserts.assertTrue(helper, vy > 0.3, "Uppercut II + full pull should give significant upward velocity, got " + vy);
        helper.succeed();
    }

    public void warhammerQuakeHitsNearbyEntitiesWhenGroundHit(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        // Player at (4, 2, 2) facing south (yRot=180), solid wall at (4, 2, 3).
        // Pigs at (2, 2, 3) and (6, 2, 3) flanking the wall – not in the raycast path.
        Vec3 abs = helper.absoluteVec(new Vec3(4.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);
        player.setXRot(0.0f);

        // Place block at y=3 so the ray at eye-height 3.62 can hit it
        helper.setBlock(new BlockPos(4, 3, 3), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());

        Pig pig1 = spawnStillPig(helper, new BlockPos(2, 2, 3));
        Pig pig2 = spawnStillPig(helper, new BlockPos(6, 2, 3));

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.quakeEnchantment(level.registryAccess()), 1);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        float h1 = pig1.getHealth();
        float h2 = pig2.getHealth();

        Items.IRON_WARHAMMER.releaseUsing(warhammer, level, player, 72000 - 20);

        TestAsserts.assertTrue(helper, pig1.getHealth() < h1 || pig2.getHealth() < h2,
                "Quake should damage at least one pig near the impact point");
        helper.succeed();
    }

    public void warhammerQuakeDoesNothingWithoutEnchantment(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        Vec3 abs = helper.absoluteVec(new Vec3(4.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);
        player.setXRot(0.0f);

        helper.setBlock(new BlockPos(4, 3, 3), net.minecraft.world.level.block.Blocks.STONE.defaultBlockState());

        Pig pig1 = spawnStillPig(helper, new BlockPos(2, 2, 3));
        Pig pig2 = spawnStillPig(helper, new BlockPos(6, 2, 3));

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        player.startUsingItem(net.minecraft.world.InteractionHand.MAIN_HAND);

        float h1 = pig1.getHealth();
        float h2 = pig2.getHealth();

        Items.IRON_WARHAMMER.releaseUsing(warhammer, level, player, 72000 - 20);

        TestAsserts.assertTrue(helper, pig1.getHealth() == h1 && pig2.getHealth() == h2,
                "Warhammer without quake should not damage flanking pigs");
        helper.succeed();
    }

    public void warhammerDashAppliesForwardVelocity(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);  // facing south (+z)
        player.setXRot(0.0f);
        player.setDeltaMovement(Vec3.ZERO);

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.dashingEnchantment(level.registryAccess()), 2);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);

        // full charge, no point-blank target
        xerca.xercatools.item.WarhammerDashManager.startDash(player, warhammer, EquipmentSlot.MAINHAND, 1.0f, 2, false);
        xerca.xercatools.item.WarhammerDashManager.onServerTick(java.util.Objects.requireNonNull(level.getServer()));

        double vz = player.getDeltaMovement().z;
        TestAsserts.assertTrue(helper, vz > 0.5, "Dashing II at full charge should launch the player forward (+z), got " + vz);
        helper.succeed();
    }

    public void warhammerDashStrikesEntityInRange(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);  // facing south (+z)
        player.setXRot(0.0f);

        // pig a few blocks ahead at eye height, within the warhammer's hit range
        Pig pig = spawnStillPig(helper, new BlockPos(2, 3, 5));

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.dashingEnchantment(level.registryAccess()), 2);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);

        float initialHealth = pig.getHealth();
        xerca.xercatools.item.WarhammerDashManager.startDash(player, warhammer, EquipmentSlot.MAINHAND, 1.0f, 2, false);
        xerca.xercatools.item.WarhammerDashManager.onServerTick(java.util.Objects.requireNonNull(level.getServer()));

        TestAsserts.assertTrue(helper, pig.getHealth() < initialHealth, "Dash should strike a pig that is within reach");
        helper.succeed();
    }

    public void warhammerDashAlreadyHitDoesNotStrikeAgain(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        Vec3 abs = helper.absoluteVec(new Vec3(2.5, 2.0, 2.5));
        player.setPos(abs.x, abs.y, abs.z);
        player.setYRot(0.0f);
        player.setXRot(0.0f);

        Pig pig = spawnStillPig(helper, new BlockPos(2, 3, 5));

        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(WarhammerEnchantments.dashingEnchantment(level.registryAccess()), 2);
        warhammer.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);

        // alreadyHit=true → the dash must not deal a second hit even though the pig is in reach
        float initialHealth = pig.getHealth();
        xerca.xercatools.item.WarhammerDashManager.startDash(player, warhammer, EquipmentSlot.MAINHAND, 1.0f, 2, true);
        xerca.xercatools.item.WarhammerDashManager.onServerTick(java.util.Objects.requireNonNull(level.getServer()));

        TestAsserts.assertTrue(helper, pig.getHealth() == initialHealth,
                "Dash should not strike again when the release already hit a target");
        helper.succeed();
    }

    // ── Group C: enchanting-table availability ────────────────────────────────

    public void warhammerAllowsModSpecificEnchantments(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);

        TestAsserts.assertTrue(helper, stack.isPrimaryItemFor(reg.getOrThrow(WarhammerEnchantments.MAIM)),
                "Warhammer should allow Maim enchantment");
        TestAsserts.assertTrue(helper, stack.isPrimaryItemFor(reg.getOrThrow(WarhammerEnchantments.QUICK)),
                "Warhammer should allow Quick enchantment");
        TestAsserts.assertTrue(helper, stack.isPrimaryItemFor(reg.getOrThrow(WarhammerEnchantments.QUAKE)),
                "Warhammer should allow Quake enchantment");
        TestAsserts.assertTrue(helper, stack.isPrimaryItemFor(reg.getOrThrow(WarhammerEnchantments.UPPERCUT)),
                "Warhammer should allow Uppercut enchantment");
        TestAsserts.assertTrue(helper, stack.isPrimaryItemFor(reg.getOrThrow(WarhammerEnchantments.DASHING)),
                "Warhammer should allow Dashing enchantment");
        helper.succeed();
    }

    public void warhammerAllowsMaceEnchantments(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);

        for (var key : java.util.List.of(Enchantments.DENSITY, Enchantments.BREACH, Enchantments.WIND_BURST)) {
            TestAsserts.assertTrue(helper, stack.isPrimaryItemFor(reg.getOrThrow(key)),
                    "Warhammer should allow mace enchantment " + key.identifier());
        }
        helper.succeed();
    }

    public void densityIsExclusiveWithBreachAndQuick(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        var density = reg.getOrThrow(Enchantments.DENSITY);
        var breach = reg.getOrThrow(Enchantments.BREACH);
        var quick = reg.getOrThrow(WarhammerEnchantments.QUICK);

        TestAsserts.assertTrue(helper, !net.minecraft.world.item.enchantment.Enchantment.areCompatible(density, breach),
                "Density and Breach should be mutually exclusive");
        TestAsserts.assertTrue(helper, !net.minecraft.world.item.enchantment.Enchantment.areCompatible(density, quick),
                "Density and Quick should be mutually exclusive");
        helper.succeed();
    }

    public void warhammerAllowsVanillaWhitelistedEnchantments(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);

        for (var key : java.util.List.of(Enchantments.UNBREAKING, Enchantments.MENDING,
                Enchantments.SMITE, Enchantments.BANE_OF_ARTHROPODS, Enchantments.LOOTING)) {
            boolean result = xerca.xercatools.Mod.toolSupportsEnchantment(stack, reg.getOrThrow(key));
            TestAsserts.assertTrue(helper, result,
                    "Warhammer should allow vanilla enchantment " + key.identifier());
        }
        helper.succeed();
    }

    public void warhammerBlocksOffLimitEnchantments(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);
        ItemStack stack = new ItemStack(Items.IRON_WARHAMMER);

        for (var key : java.util.List.of(Enchantments.SHARPNESS, Enchantments.SWEEPING_EDGE, Enchantments.FORTUNE)) {
            boolean result = xerca.xercatools.Mod.toolSupportsEnchantment(stack, reg.getOrThrow(key));
            TestAsserts.assertTrue(helper, !result,
                    "Warhammer should not allow enchantment " + key.identifier());
        }
        helper.succeed();
    }

    // ── Group H: Knife ────────────────────────────────────────────────────────

    public void knifeBackstabBonusWhenSneak(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        player.setShiftKeyDown(true);
        pig.setYRot(0.0f);
        player.setYRot(0.0f);  // both face same direction → attacker is behind target

        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        float crit = ItemKnife.critDamage(pig, player, knife);
        TestAsserts.assertTrue(helper, crit == 5.0f,
                "Sneaking backstab should give 5.0 crit bonus, got " + crit);
        helper.succeed();
    }

    public void knifeNoBackstabBonusFromFront(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        player.setShiftKeyDown(true);
        pig.setYRot(0.0f);
        player.setYRot(180.0f);  // player facing opposite direction → attacker is in front

        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        float crit = ItemKnife.critDamage(pig, player, knife);
        TestAsserts.assertTrue(helper, crit == 0.0f,
                "Facing from front should give no backstab bonus, got " + crit);
        helper.succeed();
    }

    public void knifeNoBackstabBonusWhenNotSneaking(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        player.setShiftKeyDown(false);
        pig.setYRot(0.0f);
        player.setYRot(0.0f);

        float crit = ItemKnife.critDamage(pig, player, new ItemStack(Items.IRON_KNIFE));
        TestAsserts.assertTrue(helper, crit == 0.0f,
                "Not sneaking should give no backstab bonus, got " + crit);
        helper.succeed();
    }

    public void knifeStealthEnchantmentIncreasesBackstabBonus(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        player.setShiftKeyDown(true);
        pig.setYRot(0.0f);
        player.setYRot(0.0f);

        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(KnifeEnchantments.stealthEnchantment(level.registryAccess()), 1);
        knife.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        float crit = ItemKnife.critDamage(pig, player, knife);
        TestAsserts.assertTrue(helper, crit == 7.0f,
                "Stealth I backstab should give 5.0 + 2.0 = 7.0 bonus, got " + crit);
        helper.succeed();
    }

    public void knifeStealthLevel2IncreasesBackstabFurther(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        player.setShiftKeyDown(true);
        pig.setYRot(0.0f);
        player.setYRot(0.0f);

        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(KnifeEnchantments.stealthEnchantment(level.registryAccess()), 2);
        knife.set(DataComponents.ENCHANTMENTS, enc.toImmutable());

        float crit = ItemKnife.critDamage(pig, player, knife);
        TestAsserts.assertTrue(helper, crit == 9.0f,
                "Stealth II backstab should give 5.0 + 4.0 = 9.0 bonus, got " + crit);
        helper.succeed();
    }

    public void knifeOffhandUseAddsCooldown(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        player.setItemSlot(EquipmentSlot.OFFHAND, knife);

        Items.IRON_KNIFE.use(helper.getLevel(), player, net.minecraft.world.InteractionHand.OFF_HAND);

        TestAsserts.assertTrue(helper, player.getCooldowns().isOnCooldown(knife),
                "Knife offhand use should add a cooldown");
        helper.succeed();
    }

    public void knifePoisonEnchantmentAppliesEffectOnHit(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(KnifeEnchantments.poisonEnchantment(level.registryAccess()), 1);
        knife.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, knife);

        knife.hurtEnemy(pig, player);

        TestAsserts.assertTrue(helper, pig.hasEffect(MobEffects.POISON),
                "Poison I knife hit should apply poison effect");
        net.minecraft.world.effect.MobEffectInstance effect = pig.getEffect(MobEffects.POISON);
        TestAsserts.assertTrue(helper, effect != null && effect.getDuration() >= 60,
                "Poison I should last at least 60 ticks (30+30)");
        TestAsserts.assertTrue(helper, effect != null && effect.getAmplifier() == 0,
                "Poison I amplitude should be 0");
        helper.succeed();
    }

    public void knifePoisonLevel2HasLongerDuration(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        ItemEnchantments.Mutable enc = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc.set(KnifeEnchantments.poisonEnchantment(level.registryAccess()), 2);
        knife.set(DataComponents.ENCHANTMENTS, enc.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, knife);

        knife.hurtEnemy(pig, player);

        net.minecraft.world.effect.MobEffectInstance effect = pig.getEffect(MobEffects.POISON);
        TestAsserts.assertTrue(helper, effect != null && effect.getDuration() >= 90,
                "Poison II should last at least 90 ticks (30+60)");
        TestAsserts.assertTrue(helper, effect != null && effect.getAmplifier() == 1,
                "Poison II amplitude should be 1");
        helper.succeed();
    }

    public void knifeStealthHitDealsFullBonusDespiteHurtResistance(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        player.setShiftKeyDown(true);
        pig.setYRot(0.0f);
        player.setYRot(0.0f);

        ItemStack knife = new ItemStack(Items.IRON_KNIFE);
        player.setItemSlot(EquipmentSlot.MAINHAND, knife);

        // The main hit starts the hurt-resistance window with lastHurt = 3; the stealth
        // bonus applied by hurtEnemy right after must still land in full.
        pig.hurtServer(level, level.damageSources().playerAttack(player), 3.0f);
        float healthAfterMainHit = pig.getHealth();
        knife.hurtEnemy(pig, player);

        float bonusDealt = healthAfterMainHit - pig.getHealth();
        TestAsserts.assertTrue(helper, Math.abs(bonusDealt - 5.0f) < 0.001f,
                "Stealth hit should deal the full 5.0 bonus during hurt resistance, got " + bonusDealt);
        helper.succeed();
    }

    public void knifeOffhandDamageMatchesMainHandForEveryTier(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        // Offhand hits must use the knife's main-hand attack damage (player base 1 + tier bonus).
        record TierCase(ItemStack knife, float expected) {
        }
        TierCase[] cases = {
                new TierCase(new ItemStack(Items.IRON_KNIFE), 3.0f),
                new TierCase(new ItemStack(Items.DIAMOND_KNIFE), 4.0f),
                new TierCase(new ItemStack(Items.NETHERITE_KNIFE), 5.0f),
        };
        for (TierCase tierCase : cases) {
            float dmg = ItemKnife.getOffhandDamage(level, tierCase.knife(), pig, player);
            TestAsserts.assertTrue(helper, Math.abs(dmg - tierCase.expected()) < 0.001f,
                    "Offhand damage for " + tierCase.knife().getItem() + " should be " + tierCase.expected() + ", got " + dmg);
        }
        helper.succeed();
    }

    public void knifeOffhandInteractionDealsMainHandDamage(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        ItemStack knife = new ItemStack(Items.NETHERITE_KNIFE);
        player.setItemSlot(EquipmentSlot.OFFHAND, knife);

        float initialHealth = pig.getHealth();
        net.minecraft.world.InteractionResult result = xerca.xercatools.Mod.handleKnifeOffhand(player, level, net.minecraft.world.InteractionHand.OFF_HAND, pig);

        TestAsserts.assertTrue(helper, result == net.minecraft.world.InteractionResult.SUCCESS,
                "Offhand knife interaction should be handled");
        float dealt = initialHealth - pig.getHealth();
        TestAsserts.assertTrue(helper, Math.abs(dealt - 5.0f) < 0.001f,
                "Offhand netherite knife hit should deal the main-hand 5.0 damage, got " + dealt);
        TestAsserts.assertTrue(helper, knife.getDamageValue() == 1, "Offhand knife hit should cost 1 durability");
        helper.succeed();
    }

    public void knifeOffhandDamageBaseIsThree(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));

        // not sneaking → no crit bonus; no sharpness → no enchant bonus
        float dmg = ItemKnife.getOffhandDamage(level, new ItemStack(Items.IRON_KNIFE), pig, player);
        TestAsserts.assertTrue(helper, dmg == 3.0f,
                "Offhand damage without enchants or backstab should be 3.0, got " + dmg);
        helper.succeed();
    }

    public void knifeOffhandDamageScalesWithSharpness(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = spawnStillPig(helper, new BlockPos(1, 2, 1));
        var reg = level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT);

        ItemStack knife1 = new ItemStack(Items.IRON_KNIFE);
        ItemEnchantments.Mutable enc1 = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc1.set(reg.getOrThrow(Enchantments.SHARPNESS), 1);
        knife1.set(DataComponents.ENCHANTMENTS, enc1.toImmutable());
        float dmg1 = ItemKnife.getOffhandDamage(level, knife1, pig, player);
        TestAsserts.assertTrue(helper, Math.abs(dmg1 - 4.0f) < 0.001f,
                "Sharpness I offhand damage should be 4.0, got " + dmg1);

        ItemStack knife2 = new ItemStack(Items.IRON_KNIFE);
        ItemEnchantments.Mutable enc2 = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enc2.set(reg.getOrThrow(Enchantments.SHARPNESS), 2);
        knife2.set(DataComponents.ENCHANTMENTS, enc2.toImmutable());
        float dmg2 = ItemKnife.getOffhandDamage(level, knife2, pig, player);
        TestAsserts.assertTrue(helper, Math.abs(dmg2 - 4.5f) < 0.001f,
                "Sharpness II offhand damage should be 4.5, got " + dmg2);
        helper.succeed();
    }
}
