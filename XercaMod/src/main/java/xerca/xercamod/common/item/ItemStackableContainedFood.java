package xerca.xercamod.common.item;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nonnull;

public class ItemStackableContainedFood extends Item {
    private Item container;

    public ItemStackableContainedFood(Properties properties, Item container, int stackSize) {
        super(properties.maxStackSize(stackSize));
        this.container = container;
    }

    @Override
    @NonnullDefault
    @Nonnull
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        super.onItemUseFinish(stack, worldIn, entityLiving);
        ItemStack containerStack = new ItemStack(container);
        if(stack.getCount() == 0){
            return containerStack;
        }
        else{
            if(entityLiving instanceof PlayerEntity){
                PlayerInventory inv = ((PlayerEntity)(entityLiving)).inventory;
                if(inv.storeItemStack(containerStack) == -1 && inv.getFirstEmptyStack() == -1){
                    if(!worldIn.isRemote) {
                        worldIn.addEntity(new ItemEntity(worldIn, entityLiving.getPosition().getX(), entityLiving.getPosition().getY(), entityLiving.getPosition().getZ(), containerStack));
                    }
                }else{
                    inv.addItemStackToInventory(containerStack);
                }
            }
            return stack;
        }
    }
}
