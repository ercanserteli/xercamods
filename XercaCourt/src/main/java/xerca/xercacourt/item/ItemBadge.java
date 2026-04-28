package xerca.xercacourt.item;

import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercacourt.SoundEvents;

public class ItemBadge extends Item {
    public ItemBadge() {
        super(new Properties().stacksTo(1));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack heldItem = player.getItemInHand(hand);
        level.playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.OBJECTION, SoundSource.PLAYERS, 1.0F,
                level.random.nextFloat() * 0.2F + 0.8F);
        player.getCooldowns().addCooldown(this, 20);
        return InteractionResultHolder.success(heldItem);
    }
}
