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
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xerca.xercamod.common.Config;
import xerca.xercamod.common.SoundEvents;
import xerca.xercamod.common.XercaMod;
import xerca.xercamod.common.packets.ConfettiParticlePacket;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

public class ItemConfetti extends Item {

    ItemConfetti() {
        super(new Item.Properties().tab(CreativeModeTab.TAB_MISC));
        this.setRegistryName("item_confetti");
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level worldIn, Player playerIn, @Nonnull InteractionHand hand) {
//        worldIn.playSound(playerIn, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), SoundEvents.CONFETTI, SoundCategory.PLAYERS, 1.0f, worldIn.rand.nextFloat() * 0.2F + 0.8F);
        playSound(worldIn, playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ());
        if(!worldIn.isClientSide){
            Vec3 pos = playerIn.position().add(playerIn.getLookAngle()).add(0d, 1d, 0d);
            ConfettiParticlePacket pack = new ConfettiParticlePacket(32, pos.x, pos.y, pos.z);
            XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(pos.x, pos.y, pos.z, 64.0D, playerIn.level.dimension())), pack);
        }

        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if (!playerIn.isCreative()) {
            heldItem.shrink(1);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    static public void playSound(Level world, @Nullable Player player, double x, double y, double z){
        world.playSound(player, x, y, z, SoundEvents.CONFETTI, SoundSource.PLAYERS, 1.0f, world.random.nextFloat() * 0.2F + 0.8F);
    }

    @Override
    @ParametersAreNonnullByDefault
    public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
        if(!Config.isConfettiEnabled()){
            return;
        }
        super.fillItemCategory(group, items);
    }
}
