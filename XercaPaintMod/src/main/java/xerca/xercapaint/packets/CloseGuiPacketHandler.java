package xerca.xercapaint.packets;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.client.ClientPacketHandler;

public final class CloseGuiPacketHandler {
    private CloseGuiPacketHandler() {
    }

    // Client-only handling lives in ClientPacketHandler: referencing client types here would link
    // them on the dedicated server when this handler is registered (RegisterPayloadHandlersEvent).
    public static void handle(CloseGuiPacket packet, IPayloadContext context) {
        context.enqueueWork(ClientPacketHandler::closeGui);
    }
}
