package xerca.xercapaint.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityCanvas;
import xerca.xercapaint.common.network.NetworkSender;

import java.util.function.Supplier;

public class PictureRequestPacketHandler {
    public static void handle(final PictureRequestPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            XercaPaint.LOGGER.error("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            XercaPaint.LOGGER.error("Sending player was null when CanvasUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(PictureRequestPacket msg, ServerPlayer pl) {
        String name = msg.getName();
        EntityCanvas.Picture picture = EntityCanvas.PICTURES.get(name);
        if (picture != null) {
            PictureSendPacket pack = new PictureSendPacket(name, picture.version(), picture.pixels(), picture.sidesActive(), picture.sidePixels());
            NetworkSender.sendToPlayer(pl, pack);
        }
    }
}
