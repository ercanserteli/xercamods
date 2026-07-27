package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.client.MusicClientPacketHandler;

public final class SingleNoteClientPacketHandler {
    private SingleNoteClientPacketHandler() {
    }

    public static void handle(SingleNoteClientPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> MusicClientPacketHandler.singleNote(packet));
    }
}
