package xerca.xercafood.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.SoundEvents;

import javax.annotation.Nonnull;

class ItemUltimateBurger extends Item {

    public ItemUltimateBurger() {
        super(new Item.Properties().food(Foods.ULTIMATE_BURGER));
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof Player) {
            Player entityPlayer = (Player) entityLiving;
            worldIn.playSound(null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), SoundEvents.BIG_BURP, SoundSource.PLAYERS, 5.0F, worldIn.random.nextFloat() * 0.1F + 0.9F);
            if (entityPlayer instanceof ServerPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)entityPlayer, stack);
            }
        }
        return entityLiving.eat(worldIn, stack);
    }
}
