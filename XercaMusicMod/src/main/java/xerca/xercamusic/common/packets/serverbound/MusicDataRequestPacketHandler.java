package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.sendToClient;

public final class MusicDataRequestPacketHandler {
    private static void processMessage(MusicDataRequestPacket msg, ServerPlayer pl, MinecraftServer server) {
        UUID id = msg.id();
        int version = msg.version();
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, server);
        MusicDataResponsePacket packet;
        if (data != null) {
            packet = new MusicDataResponsePacket(id, data.version(), data.notes(), data.volumeMarkers());
        } else {
            packet = new MusicDataResponsePacket(id, 0, new ArrayList<>(), null);
        }
        sendToClient(pl, packet);
    }

    public static void handle(MusicDataRequestPacket packet, IPayloadContext context) {
        ServerPlayer player = (ServerPlayer) context.player();
        context.enqueueWork(() -> processMessage(packet, player, player.level().getServer()));
    }
}