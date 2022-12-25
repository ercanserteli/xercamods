package xerca.xercaconfetti.packet;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.core.Vec3i;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import xerca.xercaconfetti.Mod;

public class ConfettiParticlePacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(ConfettiParticlePacket pkt) {
        Vec3i dir = pkt.getDirection();
        Level world = Minecraft.getInstance().level;
        if(world != null) {
            for (int j = 0; j < pkt.getCount(); ++j) {
                double velX = ((double) world.random.nextFloat() + dir.getX() - 0.5D) * 0.3D;
                double velY = ((double) world.random.nextFloat() + dir.getY() * 0.5D) * 0.5D;
                double velZ = ((double) world.random.nextFloat() + dir.getZ() - 0.5D) * 0.3D;
                world.addParticle(new ItemParticleOption(ParticleTypes.ITEM, new ItemStack(Mod.ITEM_CONFETTI)), pkt.getPosX(), pkt.getPosY(), pkt.getPosZ(), velX, velY, velZ);
            }
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ConfettiParticlePacket packet = ConfettiParticlePacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}