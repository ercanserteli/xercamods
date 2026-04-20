package xerca.xercatools.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.animal.Pig;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import xerca.xercatools.item.Items;

import net.minecraft.world.level.GameType;

public class WeaponsGameTests {
    private static final String BASIC_TEMPLATE = "xercatools:basic_test";
    private static final String WEAPONS_BATCH = "xercatools_tests";

    @GameTest(template = BASIC_TEMPLATE, batch = WEAPONS_BATCH)
    public static void warhammerDamagesEntityAndLosesDurability(GameTestHelper helper) {
        Player player = helper.makeMockPlayer(GameType.SURVIVAL);
        Pig pig = helper.spawn(EntityType.PIG, new BlockPos(1, 2, 1));
        
        ItemStack warhammer = new ItemStack(Items.ITEM_IRON_WARHAMMER);
        player.setItemSlot(EquipmentSlot.MAINHAND, warhammer);
        
        float initialHealth = pig.getHealth();
        
        // Simulate attack
        player.attack(pig);
        
        helper.assertTrue(pig.getHealth() < initialHealth, "Expected warhammer to damage the pig");
        helper.assertTrue(warhammer.getDamageValue() == 1, "Expected warhammer to lose 1 durability");
        
        helper.succeed();
    }
}
