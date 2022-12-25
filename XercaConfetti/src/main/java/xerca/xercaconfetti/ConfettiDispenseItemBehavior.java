package xerca.xercaconfetti;

import net.minecraft.core.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import xerca.xercaconfetti.item.ItemConfetti;
import xerca.xercaconfetti.packet.ConfettiParticlePacket;

import static xerca.xercaconfetti.Mod.sendToClientsAround;

public class ConfettiDispenseItemBehavior extends DefaultDispenseItemBehavior {
    @Override
    protected @NotNull ItemStack execute(@NotNull BlockSource source, ItemStack stack) {
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
        sendToClientsAround(source.getLevel(), new Vec3(x, y, z), 64, pack);
    }
}
