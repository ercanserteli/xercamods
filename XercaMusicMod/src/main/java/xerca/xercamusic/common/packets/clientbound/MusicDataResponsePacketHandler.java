package xerca.xercamusic.common.packets.clientbound;

import net.neoforged.neoforge.network.handling.IPayloadContext;

import xerca.xercamusic.client.MusicManagerClient;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;

import java.util.List;
import java.util.UUID;

public final class MusicDataResponsePacketHandler {
    private static void processMessage(MusicDataResponsePacket msg) {
        UUID id = msg.id();
        int version = msg.version();
        List<NoteEvent> notes = msg.notes();
        List<VolumeMarker> volumeMarkers = msg.volumeMarkers();
        MusicManagerClient.setMusicData(id, version, notes, volumeMarkers);
    }

    public static void handle(MusicDataResponsePacket packet, IPayloadContext context) {
        context.enqueueWork(() -> processMessage(packet));
    }
}