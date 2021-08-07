package xerca.xercamod.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemBadge extends Item {
    ItemBadge(String name) {
        super(new Item.Properties().tab(CreativeModeTab.TAB_TOOLS).stacksTo(1));
        this.setRegistryName(name);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
        final ItemStack heldItem = playerIn.getItemInHand(hand);
        worldIn.playSound(playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ(), SoundEvents.OBJECTION, SoundSource.PLAYERS, 1.0f, worldIn.random.nextFloat() * 0.2F + 0.8F);
        playerIn.getCooldowns().addCooldown(this, 20);
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isCourtroomEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
