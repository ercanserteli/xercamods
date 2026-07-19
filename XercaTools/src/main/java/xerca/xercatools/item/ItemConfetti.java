package xerca.xercatools.item;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;
import xerca.xercatools.SoundEvents;
import xerca.xercatools.packet.ConfettiParticlePacket;

import static xerca.xercatools.Mod.sendToClientsAround;

public class ItemConfetti extends Item {

    public ItemConfetti(String name) {
        super(new Item.Properties().setId(xerca.xercatools.Mod.itemKey(name)));
    }

    @Override
    public InteractionResult use(Level worldIn, Player playerIn, InteractionHand hand) {
        playSound(worldIn, playerIn, playerIn.getX(), playerIn.getY(), playerIn.getZ());
        if (!worldIn.isClientSide()) {
            Vec3 pos = playerIn.position().add(playerIn.getLookAngle()).add(0d, 1d, 0d);
            ConfettiParticlePacket pack = new ConfettiParticlePacket(pos.x, pos.y, pos.z);
            sendToClientsAround((ServerLevel) worldIn, pos, 64, pack);
        }

        final ItemStack heldItem = playerIn.getItemInHand(hand);
        if (!playerIn.isCreative()) {
            heldItem.shrink(1);
        }
        return InteractionResult.SUCCESS;
    }

    public static void playSound(Level world, @Nullable Player player, double x, double y, double z) {
        world.playSound(player, x, y, z, SoundEvents.SOUND_CONFETTI, SoundSource.PLAYERS, 1.0f, world.getRandom().nextFloat() * 0.2F + 0.8F);
    }
}
