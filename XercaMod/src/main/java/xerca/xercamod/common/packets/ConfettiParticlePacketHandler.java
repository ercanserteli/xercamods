package xerca.xercamod.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.network.NetworkEvent;
import xerca.xercamod.common.item.Items;

import java.util.function.Supplier;

public class ConfettiParticlePacketHandler {
    public static void handle(final ConfettiParticlePacket message, Supplier<NetworkEvent.Context> ctx)
    {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(ConfettiParticlePacket pkt) {
        World world = Minecraft.getInstance().world;
        for (int j = 0; j < pkt.getCount(); ++j) {
            double velX = ((double) world.rand.nextFloat() - 0.5D) * 0.3D;
            double velY = ((double) world.rand.nextFloat()) * 0.5D;
            double velZ = ((double) world.rand.nextFloat() - 0.5D) * 0.3D;
            world.addParticle(new ItemParticleData(ParticleTypes.ITEM, new ItemStack(Items.ITEM_CONFETTI)), pkt.getPosX(), pkt.getPosY(), pkt.getPosZ(), velX, velY, velZ);
        }
    }
}