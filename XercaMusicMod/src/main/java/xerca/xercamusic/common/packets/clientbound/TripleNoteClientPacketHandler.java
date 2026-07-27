package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.client.MusicClientPacketHandler;

public final class TripleNoteClientPacketHandler {
    private TripleNoteClientPacketHandler() {
    }

    public static void handle(TripleNoteClientPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> MusicClientPacketHandler.tripleNote(packet));
    }
}
