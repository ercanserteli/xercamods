package xerca.xercaconfetti;

import net.minecraft.core.Direction;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
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
        var pos = DispenserBlock.getDispensePosition(source);
        ItemConfetti.playSound(source.level(), null, pos.x(), pos.y(), pos.z());
    }

    /**
     * Order clients to display dispense particles from the specified block and facing.
     */
    protected void playAnimation(BlockSource source, Direction facingIn) {
        var pos = DispenserBlock.getDispensePosition(source);
        double x = pos.x() + facingIn.getStepX();
        double y = pos.y() + facingIn.getStepY();
        double z = pos.z() + facingIn.getStepZ();
        ConfettiParticlePacket pack = new ConfettiParticlePacket(32, x, y, z, facingIn.getNormal());
        sendToClientsAround(source.level(), new Vec3(x, y, z), 64, pack);
    }
}
