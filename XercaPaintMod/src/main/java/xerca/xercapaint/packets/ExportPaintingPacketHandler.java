package xerca.xercapaint.packets;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.client.ClientPacketHandler;

public final class ExportPaintingPacketHandler {
    private ExportPaintingPacketHandler() {
    }

    public static void handle(ExportPaintingPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.exportPainting(packet));
    }
}
