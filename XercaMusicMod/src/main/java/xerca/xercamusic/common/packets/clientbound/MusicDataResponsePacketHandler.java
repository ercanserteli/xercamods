package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.NoteEvent;

import java.util.List;
import java.util.UUID;

public class MusicDataResponsePacketHandler implements ClientPlayNetworking.PlayChannelHandler {
    private static void processMessage(MusicDataResponsePacket msg) {
        UUID id = msg.getMusicId();
        int version = msg.getVersion();
        List<NoteEvent> notes = msg.getNotes();
        MusicManagerClient.setMusicData(id, version, notes, msg.getVolumeMarkers());
    }

    @Override
    public void receive(Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) {
        MusicDataResponsePacket packet = MusicDataResponsePacket.decode(buf);
        if (packet != null) {
            client.execute(() -> processMessage(packet));
        }
    }
}
