package xerca.xercatools.packet;

import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercatools.particle.ConfettiParticles;

public final class ConfettiParticlePacketHandler {
    private ConfettiParticlePacketHandler() {
    }

    public static void handle(ConfettiParticlePacket payload, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(payload, context));
    }

    // Uses context.player().level() (typed Level) rather than Minecraft.getInstance().level
    // (typed ClientLevel): a client-only field type would link ClientLevel when this handler
    // class is verified during RegisterPayloadHandlersEvent on the dedicated server, crashing it.
    private static void processMessage(ConfettiParticlePacket pkt, IPayloadContext context) {
        Vec3i dir = pkt.direction();
        Level world = context.player().level();
        ConfettiParticles.spawnHandBurst(world, world.getRandom(), pkt.posX(), pkt.posY(), pkt.posZ(), dir);
    }
}
