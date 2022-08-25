package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import xerca.xercamusic.common.MusicManager;
import xerca.xercamusic.common.packets.clientbound.MusicDataResponsePacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.sendToClient;

public class MusicDataRequestPacketHandler implements ServerPlayNetworking.PlayChannelHandler {
    private static void processMessage(MusicDataRequestPacket msg, ServerPlayer pl) {
        UUID id = msg.getId();
        int version = msg.getVersion();
        MusicManager.MusicData data = MusicManager.getMusicData(id, version, pl.server);
        MusicDataResponsePacket packet;
        if(data != null) {
            packet = new MusicDataResponsePacket(id, data.version, data.notes);
        } else {
            packet = new MusicDataResponsePacket(id, 0, new ArrayList<>());
        }
        sendToClient(pl, packet);
    }

    @Override
    public void receive(MinecraftServer server, ServerPlayer player, ServerGamePacketListenerImpl handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MusicDataRequestPacket packet = MusicDataRequestPacket.decode(buf);
        if(packet != null){
            server.execute(()->processMessage(packet, player));
        }
    }
}
