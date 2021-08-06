package xerca.xercapaint.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import net.minecraftforge.fmllegacy.network.PacketDistributor;
import xerca.xercapaint.common.XercaPaint;
import xerca.xercapaint.common.entity.EntityCanvas;

import java.util.function.Supplier;

public class PictureRequestPacketHandler {
    public static void handle(final PictureRequestPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }
        ServerPlayer sendingPlayer = ctx.get().getSender();
        if (sendingPlayer == null) {
            System.err.println("EntityPlayerMP was null when CanvasUpdatePacket was received");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, sendingPlayer));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(PictureRequestPacket msg, ServerPlayer pl) {
        String name = msg.getName();
        EntityCanvas.Picture picture = EntityCanvas.PICTURES.get(name);
        if(picture != null){
            PictureSendPacket pack = new PictureSendPacket(name, picture.version, picture.pixels);
            XercaPaint.NETWORK_HANDLER.send(PacketDistributor.PLAYER.with(() -> pl), pack);
        }
    }
}
