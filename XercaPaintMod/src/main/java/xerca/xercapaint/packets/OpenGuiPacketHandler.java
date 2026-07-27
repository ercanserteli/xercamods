package xerca.xercapaint.packets;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.client.ClientPacketHandler;

public final class OpenGuiPacketHandler {
    private OpenGuiPacketHandler() {
    }

    public static void handle(OpenGuiPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.openGui(packet));
    }
}
