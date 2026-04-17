package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;

import java.util.List;
import java.util.UUID;

public class MusicDataResponsePacketHandler implements ClientPlayNetworking.PlayPayloadHandler<MusicDataResponsePacket> {
    private static void processMessage(MusicDataResponsePacket msg) {
        UUID id = msg.id();
        int version = msg.version();
        List<NoteEvent> notes = msg.notes();
        List<VolumeMarker> volumeMarkers = msg.volumeMarkers();
        MusicManagerClient.setMusicData(id, version, notes, volumeMarkers);
    }

    @Override
    public void receive(MusicDataResponsePacket packet, ClientPlayNetworking.Context context) {
        if (packet != null) {
            context.client().execute(() -> processMessage(packet));
        }
    }
}