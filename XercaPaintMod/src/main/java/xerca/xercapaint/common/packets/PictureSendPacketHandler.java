package xerca.xercapaint.common.packets;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xerca.xercapaint.common.entity.EntityCanvas;

import java.util.function.Supplier;

public class PictureSendPacketHandler {
    public static void handle(final PictureSendPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    @OnlyIn(Dist.CLIENT)
    private static void processMessage(PictureSendPacket msg) {
        EntityCanvas.PICTURES.put(msg.getName(), new EntityCanvas.Picture(msg.getVersion(), msg.getPixels()));
        if(!EntityCanvas.PICTURE_REQUESTS.contains(msg.getName())) {
            EntityCanvas.PICTURE_REQUESTS.remove(msg.getName());
        }
    }
}
