package xerca.xercamod.common.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.SoundEvents;

import javax.annotation.Nonnull;

public class ItemBadge extends Item {
    ItemBadge() {
        super(new Item.Properties().stacksTo(1));
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.OBJECTION.get(), SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.8F);
        playerIn.getCooldowns().addCooldown(this, 20);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }
}
