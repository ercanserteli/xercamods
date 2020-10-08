package xerca.xercamod.common;

import net.minecraft.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraftforge.fml.network.PacketDistributor;
import xerca.xercamod.common.item.ItemConfetti;
import xerca.xercamod.common.packets.ConfettiParticlePacket;

public class ConfettiDispenseItemBehavior extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack dispenseStack(IBlockSource source, ItemStack stack) {
        stack.shrink(1);
        return stack;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playDispenseSound(IBlockSource source) {
        ItemConfetti.playSound(source.getWorld(), null, source.getX(), source.getY(), source.getZ());
    }

    /**
     * Order clients to display dispense particles from the specified block and facing.
     */
    protected void spawnDispenseParticles(IBlockSource source, Direction facingIn) {
        double x = source.getX() + facingIn.getXOffset();
        double y = source.getY() + facingIn.getYOffset();
        double z = source.getZ() + facingIn.getZOffset();
        ConfettiParticlePacket pack = new ConfettiParticlePacket(32, x, y, z, facingIn.getDirectionVec());
        XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z,
                64.0D, source.getWorld().getDimensionKey())), pack);

    }
}
