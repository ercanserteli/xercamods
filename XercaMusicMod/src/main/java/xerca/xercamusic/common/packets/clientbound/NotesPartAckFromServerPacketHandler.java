package xerca.xercamusic.common.packets.clientbound;

import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.XercaMusic;

import java.util.function.Supplier;

public class NotesPartAckFromServerPacketHandler {
    public static void handle(final NotesPartAckFromServerPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            ctx.get().enqueueWork(() -> XercaMusic.onlyRunOnClient(() -> () -> xerca.xercamusic.client.ClientPacketProcessors.notesPartAck(message)));
        }
        ctx.get().setPacketHandled(true);
    }
}
