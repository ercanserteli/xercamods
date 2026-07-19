package xerca.xercafood.common.item;

import net.minecraft.advancements.triggers.CriteriaTriggers;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercafood.common.SoundEvents;

class ItemUltimateBurger extends Item {

    public ItemUltimateBurger() {
        super(new Item.Properties().setId(xerca.xercafood.common.Mod.itemKey("ultimate_burger"))
                .food(Foods.ULTIMATE_BURGER, Foods.ULTIMATE_BURGER_C));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof Player entityPlayer) {
            worldIn.playSound(null, entityPlayer.getX(), entityPlayer.getY(), entityPlayer.getZ(), SoundEvents.BIG_BURP, SoundSource.PLAYERS, 5.0F, worldIn.getRandom().nextFloat() * 0.1F + 0.9F);
            if (entityPlayer instanceof ServerPlayer serverPlayer) {
                CriteriaTriggers.CONSUME_ITEM.trigger(serverPlayer, stack);
            }
        }
        return super.finishUsingItem(stack, worldIn, entityLiving);
    }
}
