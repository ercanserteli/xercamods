package xerca.xercamod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.*;
import net.minecraft.world.World;
import xerca.xercamod.common.entity.EntityTomato;

import javax.annotation.Nonnull;

public class ItemTomato extends Item {
    public ItemTomato() {
        super(new Item.Properties().group(ItemGroup.MISC));
        this.setRegistryName("item_tomato");
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Nonnull
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        if (!playerIn.isCreative()) {
            heldItem.shrink(1);
        }

        worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.ENTITY_SNOWBALL_THROW, SoundCategory.NEUTRAL, 0.5F, 0.4F / (worldIn.rand.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isRemote) {
            EntityTomato entitytomato = new EntityTomato(worldIn, playerIn);
            entitytomato.shoot(playerIn, playerIn.rotationPitch, playerIn.rotationYaw, 0.0F, 1.5F, 1.0F);
            worldIn.addEntity(entitytomato);
        }
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }

}
