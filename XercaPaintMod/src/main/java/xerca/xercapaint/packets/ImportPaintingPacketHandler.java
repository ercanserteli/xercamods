package xerca.xercapaint.packets;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.client.ClientPacketHandler;

public final class ImportPaintingPacketHandler {
    private ImportPaintingPacketHandler() {
    }

    public static void handle(ImportPaintingPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> ClientPacketHandler.importPainting(packet));
    }
}
