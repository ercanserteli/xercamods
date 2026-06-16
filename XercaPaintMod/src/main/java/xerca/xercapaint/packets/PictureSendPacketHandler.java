package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xerca.xercapaint.entity.EntityCanvas;

public class PictureSendPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<PictureSendPacket> {
    private static void processMessage(PictureSendPacket msg) {
        EntityCanvas.PICTURES.put(msg.canvasId(), new EntityCanvas.Picture(msg.version(), msg.pixels(), msg.sidesActive(), msg.sidePixels()));
        if (EntityCanvas.isPictureRequested(msg.canvasId())) {
            EntityCanvas.clearPictureRequest(msg.canvasId());
        }
    }

    @Override
    public void receive(PictureSendPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(() -> processMessage(packet));
    }
}
