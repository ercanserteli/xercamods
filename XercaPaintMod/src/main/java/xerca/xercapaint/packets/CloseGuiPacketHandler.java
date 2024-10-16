package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.Minecraft;

public class CloseGuiPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<CloseGuiPacket> {
    private static void processMessage() {
        Minecraft.getInstance().setScreen(null);
    }

    @Override
    public void receive(CloseGuiPacket payload, ClientPlayNetworking.Context context) {
        context.client().execute(CloseGuiPacketHandler::processMessage);
    }
}
