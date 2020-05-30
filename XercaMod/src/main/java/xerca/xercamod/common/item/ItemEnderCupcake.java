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
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;
import xerca.xercamod.common.Config;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

class ItemEnderCupcake extends Item {

    public ItemEnderCupcake() {
        super(new Properties().group(ItemGroup.FOOD).food(Foods.ENDER_CUPCAKE));
        this.setRegistryName("ender_cupcake");
    }

    @Nonnull
    @Override
    public ItemStack onItemUseFinish(ItemStack stack, World world, LivingEntity entity) {
        ItemStack lvt_4_1_ = super.onItemUseFinish(stack, world, entity);
        if (!world.isRemote) {
            double lvt_5_1_ = entity.getPosition().getX();
            double lvt_7_1_ = entity.getPosition().getY();
            double lvt_9_1_ = entity.getPosition().getZ();

            for(int lvt_11_1_ = 0; lvt_11_1_ < 16; ++lvt_11_1_) {
                double x = entity.getPosition().getX() + (entity.getRNG().nextDouble() - 0.5D) * 8.0D;
                double y = MathHelper.clamp(entity.getPosition().getY() + (double)(entity.getRNG().nextInt(8) - 4), 0.0D, (world.getActualHeight() - 1));
                double z = entity.getPosition().getZ() + (entity.getRNG().nextDouble() - 0.5D) * 8.0D;
                if (entity.isPassenger()) {
                    entity.stopRiding();
                }

                if (entity.attemptTeleport(x, y, z, true)) {
                    world.playSound(null, lvt_5_1_, lvt_7_1_, lvt_9_1_, net.minecraft.util.SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
                    entity.playSound(SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, 1.0F, 1.0F);
                    break;
                }
            }

            if (entity instanceof PlayerEntity) {
                ((PlayerEntity)entity).getCooldownTracker().setCooldown(this, 20);
            }
        }

        return lvt_4_1_;
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
