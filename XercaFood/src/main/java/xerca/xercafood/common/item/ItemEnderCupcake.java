package xerca.xercafood.common.item;

import net.minecraft.core.NonNullList;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

class ItemEnderCupcake extends Item {

    public ItemEnderCupcake() {
        super(new Properties().tab(CreativeModeTab.TAB_FOOD).food(Foods.ENDER_CUPCAKE));
    }

    @Nonnull
    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        ItemStack lvt_4_1_ = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide) {
            double lvt_5_1_ = entity.getX();
            double lvt_7_1_ = entity.getY();
            double lvt_9_1_ = entity.getZ();

            for(int lvt_11_1_ = 0; lvt_11_1_ < 16; ++lvt_11_1_) {
                double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 8.0D;
                double y = Mth.clamp(entity.getY() + (double)(entity.getRandom().nextInt(8) - 4), 0.0D, (world.getHeight() - 1));
                double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 8.0D;
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }

                if (entity.randomTeleport(x, y, z, true)) {
                    world.playSound(null, lvt_5_1_, lvt_7_1_, lvt_9_1_, net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                    break;
                }
            }

            if (entity instanceof Player) {
                ((Player)entity).getCooldowns().addCooldown(this, 20);
            }
        }

        return lvt_4_1_;
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        super.fillItemCategory(group, items);
    }
}
