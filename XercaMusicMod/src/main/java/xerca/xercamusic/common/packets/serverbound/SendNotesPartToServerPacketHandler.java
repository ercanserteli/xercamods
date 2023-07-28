package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xerca.xercamusic.common.packets.clientbound.NotesPartAckFromServerPacket;
import xerca.xercamusic.common.MusicManager;

import static xerca.xercamusic.common.XercaMusic.sendToClient;

public class SendNotesPartToServerPacketHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static void processMessage(SendNotesPartToServerPacket pkt, ServerPlayer sender) {
        if(MusicManager.addNotesPart(pkt)) {
            sendToClient(sender, new NotesPartAckFromServerPacket(pkt.getUuid()));
        }
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        SendNotesPartToServerPacket packet = SendNotesPartToServerPacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}

