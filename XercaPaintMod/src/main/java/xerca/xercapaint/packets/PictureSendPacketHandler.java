package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xerca.xercapaint.entity.EntityCanvas;

public class PictureSendPacketHandler implements ClientPlayNetworking.PlayPayloadHandler<PictureSendPacket> {
    private static void processMessage(PictureSendPacket msg) {
        EntityCanvas.PICTURES.put(msg.canvasId(), new EntityCanvas.Picture(msg.version(), msg.pixels()));
        if(!EntityCanvas.PICTURE_REQUESTS.contains(msg.canvasId())) {
            EntityCanvas.PICTURE_REQUESTS.remove(msg.canvasId());
        }
    }

    @Override
    public void receive(PictureSendPacket packet, ClientPlayNetworking.Context context) {
        context.client().execute(()->processMessage(packet));
    }
}
