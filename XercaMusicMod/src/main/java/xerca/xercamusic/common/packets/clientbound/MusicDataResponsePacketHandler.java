package xerca.xercamusic.common.packets.clientbound;

import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.XercaMusic;

import java.util.function.Supplier;

public class MusicDataResponsePacketHandler {
    public static void handle(final MusicDataResponsePacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            ctx.get().enqueueWork(() -> XercaMusic.onlyRunOnClient(() -> () -> xerca.xercamusic.client.ClientPacketProcessors.musicDataResponse(message)));
        }
        ctx.get().setPacketHandled(true);
    }
}
