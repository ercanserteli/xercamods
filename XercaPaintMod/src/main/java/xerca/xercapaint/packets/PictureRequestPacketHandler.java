package xerca.xercapaint.packets;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import xerca.xercapaint.entity.EntityCanvas;

public class PictureRequestPacketHandler implements ServerPlayNetworking.PlayPayloadHandler<PictureRequestPacket> {
    private static void processMessage(PictureRequestPacket msg, ServerPlayer pl) {
        String canvasId = msg.canvasId();
        EntityCanvas.Picture picture = EntityCanvas.PICTURES.get(canvasId);
        if(picture != null){
            PictureSendPacket pack = new PictureSendPacket(canvasId, picture.version, picture.pixels);
            ServerPlayNetworking.send(pl, pack);
        }
    }

    @Override
    public void receive(PictureRequestPacket packet, ServerPlayNetworking.Context context) {
        context.server().execute(()->processMessage(packet, context.player()));
    }
}
