package xerca.xercapaint.tests;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.gametest.framework.GameTest;
import net.minecraft.gametest.framework.GameTestHelper;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.gametest.GameTestHolder;
import net.minecraftforge.gametest.PrefixGameTestTemplate;
import xerca.xercapaint.common.entity.Entities;
import xerca.xercapaint.common.entity.EntityEasel;
import xerca.xercapaint.common.item.Items;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static xerca.xercapaint.common.XercaPaint.MODID;
import static xerca.xercapaint.tests.TestUtils.chainTasks;

@GameTestHolder(MODID)
public class PaintingGameTests {

    @GameTest(template = "dummytest")
    @PrefixGameTestTemplate(false)
    public static void dummyTest(GameTestHelper helper) {
        final BlockPos easelLand = new BlockPos(3, 1, 2);
        AtomicReference<EntityEasel> easel = new AtomicReference<>();

        Player player = helper.makeMockSurvivalPlayer();
        player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_EASEL.get(), 1));
        chainTasks(helper, List.of(
                () -> player.moveTo(easelLand.east(), 0, 0),
                () -> helper.assertTrue(player.getMainHandItem().is(Items.ITEM_EASEL.get()), "Player isn't holding an easel!"),
                () -> helper.placeAt(player, player.getMainHandItem(), easelLand, Direction.UP),
                new TestUtils.TestDelay(5),
                () -> {
                    helper.assertEntityPresent(Entities.EASEL.get(), easelLand.above());
                    List<? extends Entity> list = helper.getLevel().getEntities(Entities.EASEL.get(), new AABB(helper.absolutePos(easelLand.above())), Entity::isAlive);
                    easel.set((EntityEasel) list.get(0));
                },
                () -> player.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.ITEM_CANVAS.get(), 1)),
                () -> helper.assertTrue(player.getMainHandItem().is(Items.ITEM_CANVAS.get()), "Player isn't holding a canvas!"),
                () -> player.interactOn(easel.get(), InteractionHand.MAIN_HAND),
                () -> helper.assertTrue(easel.get().getItem().is(Items.ITEM_CANVAS.get()), "There is no canvas on the easel!"),
                helper::succeed
        ));

    }
}
