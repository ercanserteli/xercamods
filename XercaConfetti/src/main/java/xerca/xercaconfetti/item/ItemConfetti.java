package xerca.xercaconfetti.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import xerca.xercaconfetti.Mod;
import xerca.xercaconfetti.packet.ConfettiParticlePacket;

import static xerca.xercaconfetti.Mod.sendToClientsAround;

public class ItemConfetti extends Item {

    public ItemConfetti() {
        super(new Item.Properties());
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level worldIn, @NotNull Player playerIn, @NotNull InteractionHand hand) {
        playSound(worldIn, playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ());
        if(!worldIn.isClientSide){
            Vec3 pos = playerIn.position().add(playerIn.getLookAngle()).add(0d, 1d, 0d);
            ConfettiParticlePacket pack = new ConfettiParticlePacket(32, pos.x, pos.y, pos.z);
            sendToClientsAround((ServerLevel) worldIn, pos, 64, pack);
        }

        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if (!playerIn.isCreative()) {
            heldItem.shrink(1);
        }
        return new InteractionResultHolder<>(InteractionResult.SUCCESS, heldItem);
    }

    static public void playSound(Level world, @Nullable Player player, double x, double y, double z){
        world.playSound(player, x, y, z, Mod.SOUND_CONFETTI, SoundSource.PLAYERS, 1.0f, world.random.nextFloat() * 0.2F + 0.8F);
    }
}
