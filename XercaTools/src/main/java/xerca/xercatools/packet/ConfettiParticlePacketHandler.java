package xerca.xercatools.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercatools.particle.ConfettiParticles;

public final class ConfettiParticlePacketHandler {
    private ConfettiParticlePacketHandler() {
    }

    public static void handle(ConfettiParticlePacket payload, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(payload));
    }

    private static void processMessage(ConfettiParticlePacket pkt) {
        Vec3i dir = pkt.direction();
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            ConfettiParticles.spawnHandBurst(world, world.getRandom(), pkt.posX(), pkt.posY(), pkt.posZ(), dir);
        }
    }
}
