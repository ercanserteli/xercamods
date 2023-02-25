package xerca.xercamusic.common.packets;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;
import xerca.xercamusic.common.CommandImport;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.XercaMusic;

import java.util.function.Supplier;

public class SendNotesPartToServerPacketHandler {
    public static void handle(final SendNotesPartToServerPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message, ctx.get().getSender()));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(SendNotesPartToServerPacket pkt, ServerPlayer sender) {
        if(MusicManager.addNotesPart(pkt)) {
            PacketDistributor.PacketTarget target = PacketDistributor.PLAYER.with(()->sender);
            XercaMusic.NETWORK_HANDLER.send(target, new NotesPartAckFromServerPacket(pkt.getUuid()));
        }
    }
}
