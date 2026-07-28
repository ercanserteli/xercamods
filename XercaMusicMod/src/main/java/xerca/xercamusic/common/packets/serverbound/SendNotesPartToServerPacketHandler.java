package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.packets.clientbound.NotesPartAckFromServerPacket;

import static xerca.xercamusic.common.XercaMusic.sendToClient;

import java.util.function.Supplier;

public class SendNotesPartToServerPacketHandler {
    private static void processMessage(SendNotesPartToServerPacket pkt, ServerPlayer sender) {
        if (MusicManager.addNotesPart(pkt)) {
            sendToClient(sender, new NotesPartAckFromServerPacket(pkt.getUuid()));
        }
    }
    public static void handle(final SendNotesPartToServerPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            ServerPlayer sender = ctx.get().getSender();
            ctx.get().enqueueWork(() -> processMessage(message, sender));
        }
        ctx.get().setPacketHandled(true);
    }
}

