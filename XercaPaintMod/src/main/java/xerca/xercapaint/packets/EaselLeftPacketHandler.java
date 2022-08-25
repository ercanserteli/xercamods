package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.entity.Entity;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityEasel;

public class EaselLeftPacketHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static void processMessage(EaselLeftPacket msg, ServerPlayer pl) {
        if(msg.getEaselId() > -1){
            Entity entityEasel = pl.level.getEntity(msg.getEaselId());
            if(entityEasel == null){
                Mod.LOGGER.error("EaselLeftPacket: Easel entity not found! easelId: " + msg.getEaselId());
                return;
            }
            if(!(entityEasel instanceof EntityEasel easel)){
                Mod.LOGGER.error("EaselLeftPacket: Entity found is not an easel! easelId: " + msg.getEaselId());
                return;
            }
            easel.setPainter(null);
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        EaselLeftPacket packet = EaselLeftPacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
