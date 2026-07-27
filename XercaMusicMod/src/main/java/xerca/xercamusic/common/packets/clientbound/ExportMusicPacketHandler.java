package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.client.MusicClientPacketHandler;

public final class ExportMusicPacketHandler {
    private ExportMusicPacketHandler() {
    }

    public static void handle(ExportMusicPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> MusicClientPacketHandler.exportMusic(packet));
    }
}
