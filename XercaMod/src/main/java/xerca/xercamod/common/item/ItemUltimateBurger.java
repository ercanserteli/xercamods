package xerca.xercamod.common.item;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.world.World;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.SoundEvents;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

class ItemUltimateBurger extends Item {

    public ItemUltimateBurger() {
        super(new Item.Properties().group(ItemGroup.FOOD).food(Foods.ULTIMATE_BURGER));
        this.setRegistryName("item_ultimate_burger");
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World worldIn, LivingEntity entityLiving) {
        if (entityLiving instanceof PlayerEntity) {
            PlayerEntity entityPlayer = (PlayerEntity) entityLiving;
            worldIn.playSound(null, entityPlayer.getPosX(), entityPlayer.getPosY(), entityPlayer.getPosZ(), SoundEvents.BIG_BURP, SoundCategory.PLAYERS, 5.5F, worldIn.rand.nextFloat() * 0.1F + 0.9F);
            if (entityPlayer instanceof ServerPlayerEntity) {
                CriteriaTriggers.CONSUME_ITEM.trigger((ServerPlayerEntity)entityPlayer, stack);
            }
        }
        return entityLiving.onFoodEaten(worldIn, stack);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemGroup(ItemGroup group, NonNullList<ItemStack> items) {
        if(!Config.isFoodEnabled()){
            return;
        }
        super.fillItemGroup(group, items);
    }
}
