package xerca.xercamod.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public class BeheadParticlePacketHandler {
    public static void handle(final BeheadParticlePacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(BeheadParticlePacket pkt) {
        World world = Minecraft.getInstance().world;
        for (int j = 0; j < pkt.getCount(); ++j) {
            double velX = ((double) world.rand.nextFloat() - 0.5D) * 0.25D;
            double velY = 0.4D + ((double) world.rand.nextFloat()) * 0.3D;
            double velZ = ((double) world.rand.nextFloat() - 0.5D) * 0.25D;
            world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.NETHER_WART_BLOCK)), pkt.getPosX(), pkt.getPosY(), pkt.getPosZ(), velX, velY, velZ);
        }
    }
}