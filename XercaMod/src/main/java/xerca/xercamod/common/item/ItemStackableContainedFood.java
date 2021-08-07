package xerca.xercamod.common.item;

import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nonnull;

public class ItemStackableContainedFood extends Item {
    private Item container;

    public ItemStackableContainedFood(Properties properties, Item container, int stackSize) {
        super(properties.stacksTo(stackSize));
        this.container = container;
    }

    @Override
    @NonnullDefault
    @Nonnull
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        super.finishUsingItem(stack, worldIn, entityLiving);
        ItemStack containerStack = new ItemStack(container);
        if(stack.getCount() == 0){
            return containerStack;
        }
        else{
            if(entityLiving instanceof Player){
                Inventory inv = ((Player)(entityLiving)).getInventory();
                if(inv.getSlotWithRemainingSpace(containerStack) == -1 && inv.getFreeSlot() == -1){
                    if(!worldIn.isClientSide) {
                        worldIn.addFreshEntity(new ItemEntity(worldIn, entityLiving.getX(), entityLiving.getY(), entityLiving.getZ(), containerStack));
                    }
                }else{
                    inv.add(containerStack);
                }
            }
            return stack;
        }
    }
}
