package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.function.Supplier;
import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.sendToClient;

public class MusicDataRequestPacketHandler {
    private static void processMessage(MusicDataRequestPacket msg, ServerPlayer pl) {
        UUID id = msg.getMusicId();
        int version = msg.getVersion();
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, pl.server);
        MusicDataResponsePacket packet;
        if (data != null) {
            packet = new MusicDataResponsePacket(id, data.version(), data.notes(), data.volumeMarkers());
        } else {
            packet = new MusicDataResponsePacket(id, 0, new ArrayList<>());
        }
        sendToClient(pl, packet);
    }
    public static void handle(final MusicDataRequestPacket message, Supplier<NetworkEvent.Context> ctx) {
        if (message != null) {
            ServerPlayer sender = ctx.get().getSender();
            ctx.get().enqueueWork(() -> processMessage(message, sender));
        }
        ctx.get().setPacketHandled(true);
    }
}
