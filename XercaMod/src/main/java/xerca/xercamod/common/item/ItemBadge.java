package xerca.xercamod.common.item;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import xerca.xercamod.common.SoundEvents;

import javax.annotation.Nonnull;

public class ItemBadge extends Item {
    ItemBadge(String name) {
        super(new Item.Properties().group(ItemGroup.TOOLS).maxStackSize(1));
        this.setRegistryName(name);
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, @Nonnull Hand hand) {
        final ItemStack heldItem = playerIn.getHeldItem(hand);
        worldIn.playSound(playerIn, playerIn.posX, playerIn.posY, playerIn.posZ, SoundEvents.OBJECTION, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.8F);
        playerIn.getCooldownTracker().setCooldown(this, 20);
        return new ActionResult<>(ActionResultType.SUCCESS, heldItem);
    }
}
