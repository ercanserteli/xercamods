package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xerca.xercapaint.Mod;
import xerca.xercapaint.entity.EntityCanvas;

public class PictureRequestPacketHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static void processMessage(PictureRequestPacket msg, ServerPlayer pl) {
        String name = msg.getName();
        EntityCanvas.Picture picture = EntityCanvas.PICTURES.get(name);
        if(picture != null){
            PictureSendPacket pack = new PictureSendPacket(name, picture.version, picture.pixels);
            ServerPlayNetworking.send(pl, Mod.PICTURE_SEND_PACKET_ID, pack.encode());
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        PictureRequestPacket packet = PictureRequestPacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
