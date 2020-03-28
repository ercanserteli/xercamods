package xerca.xercamod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.entity.EntityConfettiBall;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemConfettiBall extends Item {

    ItemConfettiBall() {
        super(new Item.Properties().group(ItemGroup.MISC));
        this.setRegistryName("item_confetti_ball");
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if (!playerIn.isCreative()) {
            heldItem.shrink(1);
        }

        worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EntityConfettiBall entityball = new EntityConfettiBall(worldIn, playerIn);
            entityball.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.addEntity(entityball);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isConfettiEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
