package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.entity.EntityConfettiBall;

import javax.annotation.ParametersAreNonnullByDefault;

public class ItemConfettiBall extends Item {

    ItemConfettiBall() {
        super(new Item.Properties());
    }

    /**
     * Called whenever this item is equipped and the right mouse button is pressed. Args: itemStack, world, entityPlayer
     */
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, Player playerIn, @NotNull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if (!playerIn.isCreative()) {
            heldItem.shrink(1);
        }

        worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.SNOWBALL_THROW, SoundSource.NEUTRAL, 0.5F, 0.4F / (worldIn.random.nextFloat() * 0.4F + 0.8F));

        if (!worldIn.isClientSide) {
            EntityConfettiBall entityball = new EntityConfettiBall(worldIn, playerIn);
            entityball.shootFromRotation(playerIn, playerIn.getXRot(), playerIn.getYRot(), 0.0F, 1.5F, 1.0F);
            worldIn.addFreshEntity(entityball);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }
}
