package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;

public class CloseGuiPacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        client.execute(CloseGuiPacketHandler::processMessage);
    }
}
