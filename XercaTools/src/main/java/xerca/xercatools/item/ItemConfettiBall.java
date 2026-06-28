package xerca.xercatools.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercatools.entity.EntityConfettiBall;

public class ItemConfettiBall extends Item {

    public ItemConfettiBall(String name) {
        super(new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)));
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            EntityConfettiBall entityball = new EntityConfettiBall(worldIn, playerIn);
            entityball.setItem(heldItem.copyWithCount(1));
            entityball.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F, 1.0F);
            if (worldIn.addFreshEntity(entityball) && !playerIn.isCreative()) {
                heldItem.shrink(1);
            }
        }
        return InteractionResult.SUCCESS;
    }
}
