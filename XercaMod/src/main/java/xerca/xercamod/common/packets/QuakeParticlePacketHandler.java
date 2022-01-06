package xerca.xercamod.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class QuakeParticlePacketHandler {
    public static void handle(final QuakeParticlePacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(QuakeParticlePacket pkt) {
        Level world = Minecraft.getInstance().level;
        Vec3 centerPos = new Vec3(pkt.getPosX(), pkt.getPosY(), pkt.getPosZ());
        for (int j = 0; j < pkt.getCount(); ++j) {
            double posX = centerPos.x + world.random.nextGaussian();
            double posY = centerPos.y;
            double posZ = centerPos.z + world.random.nextGaussian();

            Vec3 particlePos = new Vec3(posX, posY, posZ);
            Vec3 particleVel = particlePos.subtract(centerPos).normalize().scale(0.15);

            world.addParticle(ParticleTypes.SMOKE, posX, posY, posZ, particleVel.x, 0.01, particleVel.z);
        }
    }
}