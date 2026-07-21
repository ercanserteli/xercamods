package xerca.xercapaint.packets;

import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public final class CloseGuiPacketHandler {
    private CloseGuiPacketHandler() {
    }

    private static void processMessage() {
        Minecraft.getInstance().setScreen(null);
    }

    public static void handle(CloseGuiPacket payload, IPayloadContext context) {
        context.enqueueWork(CloseGuiPacketHandler::processMessage);
    }
}
