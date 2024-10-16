package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.level.ServerPlayer;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.sendToClient;

public class MusicDataRequestPacketHandler implements ServerPlayNetworking.PlayPayloadHandler<MusicDataRequestPacket> {
    private static void processMessage(MusicDataRequestPacket msg, ServerPlayer pl) {
        UUID id = msg.id();
        int version = msg.version();
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, pl.server);
        MusicDataResponsePacket packet;
        if(data != null) {
            packet = new MusicDataResponsePacket(id, data.version(), data.notes());
        } else {
            packet = new MusicDataResponsePacket(id, 0, new ArrayList<>());
        }
        sendToClient(pl, packet);
    }

    @Override
    public void receive(MusicDataRequestPacket packet, ServerPlayNetworking.Context context) {
        if(packet != null){
            context.server().execute(()->processMessage(packet, context.player()));
        }
    }
}
