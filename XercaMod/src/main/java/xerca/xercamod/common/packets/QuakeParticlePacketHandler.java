package xerca.xercamod.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

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
        World world = Minecraft.getInstance().world;
        Vector3d centerPos = new Vector3d(pkt.getPosX(), pkt.getPosY(), pkt.getPosZ());
        for (int j = 0; j < pkt.getCount(); ++j) {
            double posX = centerPos.x + world.rand.nextGaussian();
            double posY = centerPos.y;
            double posZ = centerPos.z + world.rand.nextGaussian();

            Vector3d particlePos = new Vector3d(posX, posY, posZ);
            Vector3d particleVel = particlePos.subtract(centerPos).normalize().scale(0.15);

            world.addParticle(ParticleTypes.SMOKE, posX, posY, posZ, particleVel.x, 0.01, particleVel.z);
        }
    }
}