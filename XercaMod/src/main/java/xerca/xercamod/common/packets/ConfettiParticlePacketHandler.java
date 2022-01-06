package xerca.xercamod.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkEvent;
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
        Vec3i dir = pkt.getDirection();
        Level world = Minecraft.getInstance().level;
        for (int j = 0; j < pkt.getCount(); ++j) {
            double velX = ((double) world.random.nextFloat() + dir.getX() - 0.5D) * 0.3D;
            double velY = ((double) world.random.nextFloat() + dir.getY() * 0.5D) * 0.5D;
            double velZ = ((double) world.random.nextFloat() + dir.getZ() - 0.5D) * 0.3D;
            world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Items.ITEM_CONFETTI)), pkt.getPosX(), pkt.getPosY(), pkt.getPosZ(), velX, velY, velZ);
        }
    }
}