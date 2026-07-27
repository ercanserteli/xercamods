package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.client.MusicClientPacketHandler;

public final class MusicBoxUpdatePacketHandler {
    private MusicBoxUpdatePacketHandler() {
    }

    public static void handle(MusicBoxUpdatePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> MusicClientPacketHandler.musicBoxUpdate(packet));
    }
}
