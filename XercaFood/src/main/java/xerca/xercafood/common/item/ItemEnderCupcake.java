package xerca.xercafood.common.item;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

class ItemEnderCupcake extends Item {

    public ItemEnderCupcake() {
        super(new Properties().setId(xerca.xercafood.common.Mod.itemKey("ender_cupcake")).food(Foods.ENDER_CUPCAKE));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
        ItemStack resultStack = super.finishUsingItem(stack, world, entity);
        if (!world.isClientSide()) {
            double entityX = entity.getX();
            double entityY = entity.getY();
            double entityZ = entity.getZ();

            for (int teleportAttempt = 0; teleportAttempt < 16; ++teleportAttempt) {
                double x = entity.getX() + (entity.getRandom().nextDouble() - 0.5D) * 8.0D;
                double y = Mth.clamp(entity.getY() + (entity.getRandom().nextInt(8) - 4), 0.0D, (world.getHeight() - 1));
                double z = entity.getZ() + (entity.getRandom().nextDouble() - 0.5D) * 8.0D;
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }

                if (entity.randomTeleport(x, y, z, true)) {
                    world.playSound(null, entityX, entityY, entityZ, net.minecraft.sounds.SoundEvents.CHORUS_FRUIT_TELEPORT, SoundSource.PLAYERS, 1.0F, 1.0F);
                    entity.playSound(SoundEvents.CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                    break;
                }
            }

            if (entity instanceof Player player) {
                player.getCooldowns().addCooldown(stack, 20);
            }
        }

        return resultStack;
    }
}
