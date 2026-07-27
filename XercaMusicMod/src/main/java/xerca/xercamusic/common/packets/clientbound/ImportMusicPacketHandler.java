package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.client.MusicClientPacketHandler;

public final class ImportMusicPacketHandler {
    private ImportMusicPacketHandler() {
    }

    public static void handle(ImportMusicPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> MusicClientPacketHandler.importMusic(packet));
    }
}
