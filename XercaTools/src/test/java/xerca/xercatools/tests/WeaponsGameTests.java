package xerca.xercatools.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.GameType;
import net.minecraft.world.phys.Vec3;
import xerca.xercatools.enchantment.ScytheEnchantments;
import xerca.xercatools.entity.EntityHealthOrb;
import xerca.xercatools.item.Items;

public class WeaponsGameTests {
    private static final String BASIC_TEMPLATE = "xercatools:basic_test";
    private static final String WEAPONS_BATCH = "xercatools_tests";

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void warhammerDamagesEntityAndLosesDurability(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 2, 1));
        
        ItemStack warhammer = new ItemStack(Items.IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        
        float initialHealth = pig.getHealth();
        
        // Simulate attack
        player.attack(pig);
        
        helper.assertTrue(pig.getHealth() < initialHealth, "Expected warhammer to damage the pig");
        helper.assertTrue(warhammer.getDamageValue() == 1, "Expected warhammer to lose 1 durability");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void devourKillSpawnsHealthOrbs(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 2, 1));

        ItemStack scythe = new ItemStack(Items.IRON_SCYTHE);
        ItemEnchantments.Mutable enchantments = new ItemEnchantments.Mutable(ItemEnchantments.EMPTY);
        enchantments.set(ScytheEnchantments.devour(level.registryAccess()), 3);
        scythe.set(DataComponents.ENCHANTMENTS, enchantments.toImmutable());
        player.setItemSlot(EquipmentSlot.MAINHAND, scythe);

        pig.hurt(level.damageSources().generic(), 100.0f);
        scythe.hurtEnemy(pig, player);

        boolean orbsSpawned = !level.getEntitiesOfClass(EntityHealthOrb.class, pig.getBoundingBox().inflate(10)).isEmpty();
        helper.assertTrue(orbsSpawned, "Expected health orbs to spawn when devour kill is triggered");

        helper.succeed();
    }

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void healthOrbHealsPlayerOnTouch(GameTestHelper helper) {
        ServerLevel level = helper.getLevel();
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);

        float initialHealth = player.getMaxHealth() - 4.0f;
        player.setHealth(initialHealth);

        Vec3 pos = helper.absoluteVec(new Vec3(1, 2, 1));
        EntityHealthOrb orb = new EntityHealthOrb(level, pos.x, pos.y, pos.z, null, player);
        level.addFreshEntity(orb);

        orb.playerTouch(player);

        helper.assertTrue(player.getHealth() > initialHealth, "Expected health orb to restore player health on touch");

        helper.succeed();
    }
}
