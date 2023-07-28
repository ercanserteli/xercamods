package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.entity.EntityCanvas;

public class PictureSendPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(PictureSendPacket msg) {
        EntityCanvas.PICTURES.put(msg.getName(), new EntityCanvas.Picture(msg.getVersion(), msg.getPixels()));
        if(!EntityCanvas.PICTURE_REQUESTS.contains(msg.getName())) {
            EntityCanvas.PICTURE_REQUESTS.remove(msg.getName());
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        PictureSendPacket packet = PictureSendPacket.decode(buf);
        if(packet != null) {
            client.execute(()->processMessage(packet));
        }
    }
}
