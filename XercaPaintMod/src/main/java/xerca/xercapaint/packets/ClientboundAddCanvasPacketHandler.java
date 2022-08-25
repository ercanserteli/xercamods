package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercapaint.entity.Entities;
import xerca.xercapaint.entity.EntityCanvas;

public class ClientboundAddCanvasPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(ClientboundAddCanvasPacket msg, Minecraft client) {
        EntityCanvas entity = Entities.CANVAS.create(client.level);
        if (entity != null) {
            entity.recreateFromPacket(msg);
            int i = msg.getId();
            client.level.putNonPlayerEntity(i, entity);
        }
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        ClientboundAddCanvasPacket packet = new ClientboundAddCanvasPacket(buf);
        client.execute(()->processMessage(packet, client));
    }
}
