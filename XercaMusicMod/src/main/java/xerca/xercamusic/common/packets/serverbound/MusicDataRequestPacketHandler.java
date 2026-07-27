package xerca.xercamusic.common.packets.serverbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.server.level.ServerPlayer;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.sendToClient;

public final class MusicDataRequestPacketHandler {
    private static void processMessage(MusicDataRequestPacket msg, ServerPlayer pl) {
        UUID id = msg.id();
        int version = msg.version();
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, pl.server);
        MusicDataResponsePacket packet;
        if (data != null) {
            packet = new MusicDataResponsePacket(id, data.version(), data.notes(), data.volumeMarkers());
        } else {
            packet = new MusicDataResponsePacket(id, 0, new ArrayList<>(), null);
        }
        sendToClient(pl, packet);
    }

    public static void handle(MusicDataRequestPacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet, (ServerPlayer) context.player()));
    }
}