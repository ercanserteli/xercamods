package xerca.xercamod.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;

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
        Level world = Minecraft.getInstance().level;
        if(world != null) {
            for (int j = 0; j < pkt.getCount(); ++j) {
                double velX = ((double) world.random.nextFloat() - 0.5D) * 0.25D;
                double velY = 0.4D + ((double) world.random.nextFloat()) * 0.3D;
                double velZ = ((double) world.random.nextFloat() - 0.5D) * 0.25D;
                world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.NETHER_WART_BLOCK)), pkt.getPosX(), pkt.getPosY(), pkt.getPosZ(), velX, velY, velZ);
            }
        }
    }
}