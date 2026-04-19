package xerca.xercaconfetti.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercaconfetti.Mod;

public class ConfettiParticlePacketHandler implements ClientPlayNetworking.PlayPayloadHandler<ConfettiParticlePacket> {
    private static void processMessage(ConfettiParticlePacket pkt) {
        Vec3i dir = pkt.direction();
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            for (int j = 0; j < pkt.count(); ++j) {
                double velX = ((double) world.random.nextFloat() + dir.getX() - 0.5D) * 0.3D;
                double velY = ((double) world.random.nextFloat() + dir.getY() * 0.5D) * 0.5D;
                double velZ = ((double) world.random.nextFloat() + dir.getZ() - 0.5D) * 0.3D;
                world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Mod.ITEM_CONFETTI)), pkt.posX(), pkt.posY(), pkt.posZ(), velX, velY, velZ);
            }
        }
    }

    @Override
    public void receive(ConfettiParticlePacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> processMessage(payload));
    }
}
