package xerca.xercapaint.packets;

import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercapaint.entity.EntityCanvas;

public final class PictureRequestPacketHandler {
    private PictureRequestPacketHandler() {
    }

    private static void processMessage(PictureRequestPacket msg, ServerPlayer pl) {
        String canvasId = msg.canvasId();
        EntityCanvas.Picture picture = EntityCanvas.PICTURES.get(canvasId);
        if (picture != null) {
            PictureSendPacket pack = new PictureSendPacket(canvasId, picture.version(), picture.pixels(), picture.sidesActive(), picture.sidePixels());
            PacketDistributor.sendToPlayer(pl, pack);
        }
    }

    public static void handle(PictureRequestPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet, (ServerPlayer) context.player()));
    }
}
