package xerca.xercamod.common;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamod.common.item.ItemConfetti;
import xerca.xercamod.common.packets.ConfettiParticlePacket;

public class ConfettiDispenseItemBehavior extends DefaultDispenseItemBehavior {
    @Override
    protected ItemStack execute(BlockSource source, ItemStack stack) {
        stack.shrink(1);
        return stack;
    }

    /**
     * Play the dispense sound from the specified block.
     */
    protected void playSound(BlockSource source) {
        ItemConfetti.playSound(source.getLevel(), null, source.x(), source.y(), source.z());
    }

    /**
     * Order clients to display dispense particles from the specified block and facing.
     */
    protected void playAnimation(BlockSource source, Direction facingIn) {
        double x = source.x() + facingIn.getStepX();
        double y = source.y() + facingIn.getStepY();
        double z = source.z() + facingIn.getStepZ();
        ConfettiParticlePacket pack = new ConfettiParticlePacket(32, x, y, z, facingIn.getNormal());
        XercaMod.NETWORK_HANDLER.send(PacketDistributor.NEAR.with(() -> new PacketDistributor.TargetPoint(x, y, z,
                64.0D, source.getLevel().dimension())), pack);

    }
}
