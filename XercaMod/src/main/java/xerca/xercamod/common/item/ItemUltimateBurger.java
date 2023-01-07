package xerca.xercamod.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

class ItemUltimateBurger extends Item {

    public ItemUltimateBurger() {
        super(new Item.Properties().food(Foods.ULTIMATE_BURGER));
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(@NotNull ItemStack stack, @NotNull Level worldIn, @NotNull LivingEntity entityLiving) {
        if (entityLiving instanceof Player entityPlayer) {
            worldIn.playSound(null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), SoundEvents.BIG_BURP.get(), SoundSource.PLAYERS, 5.0F, worldIn.random.nextFloat() * 0.1F + 0.9F);
            if (entityPlayer instanceof ServerPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayer)entityPlayer, stack);
            }
        }
        return entityLiving.eat(worldIn, stack);
    }
}
