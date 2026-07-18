package xerca.xercatools.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import xerca.xercatools.particle.ConfettiParticles;

public class ConfettiParticlePacketHandler implements ClientPlayNetworking.PlayPayloadHandler<ConfettiParticlePacket> {
    private static void processMessage(ConfettiParticlePacket pkt) {
        Vec3i dir = pkt.direction();
        Level world = Minecraft.getInstance().level;
        if (world != null) {
            ConfettiParticles.spawnHandBurst(world, world.getRandom(), pkt.posX(), pkt.posY(), pkt.posZ(), dir);
        }
    }

    @Override
    public void receive(ConfettiParticlePacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(() -> processMessage(payload));
    }
}
