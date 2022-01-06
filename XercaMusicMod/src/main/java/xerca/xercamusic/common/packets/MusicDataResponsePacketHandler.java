package xerca.xercamusic.common.packets;

import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.UUID;
import java.util.function.Supplier;

public class MusicDataResponsePacketHandler {
    public static void handle(final MusicDataResponsePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (!message.isMessageValid()) {
            System.err.println("Packet was invalid");
            return;
        }

        ctx.get().enqueueWork(() -> processMessage(message));
        ctx.get().setPacketHandled(true);
    }

    private static void processMessage(MusicDataResponsePacket msg) {
        UUID id = msg.getId();
        int version = msg.getVersion();
        ArrayList<NoteEvent> notes = msg.getNotes();
        MusicManagerClient.setMusicData(id, version, notes);
    }
}
