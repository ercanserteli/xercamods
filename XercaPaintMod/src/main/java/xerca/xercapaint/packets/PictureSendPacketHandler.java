package xerca.xercapaint.packets;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.entity.EntityCanvas;

public final class PictureSendPacketHandler {
    private PictureSendPacketHandler() {
    }

    private static void processMessage(PictureSendPacket msg) {
        EntityCanvas.PICTURES.put(msg.canvasId(), new EntityCanvas.Picture(msg.version(), msg.pixels(), msg.sidesActive(), msg.sidePixels()));
        if (EntityCanvas.isPictureRequested(msg.canvasId())) {
            EntityCanvas.clearPictureRequest(msg.canvasId());
        }
    }

    public static void handle(PictureSendPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }
}
