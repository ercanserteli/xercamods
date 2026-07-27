package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.client.MusicClientPacketHandler;

public final class MusicDataResponsePacketHandler {
    private MusicDataResponsePacketHandler() {
    }

    public static void handle(MusicDataResponsePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> MusicClientPacketHandler.musicDataResponse(packet));
    }
}
