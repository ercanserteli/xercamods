package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record MusicDataResponsePacket(UUID id, int version, List<NoteEvent> notes, List<VolumeMarker> volumeMarkers) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MusicDataResponsePacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("music_data_response"));
    public static final StreamCodec<FriendlyByteBuf, MusicDataResponsePacket> PACKET_CODEC = StreamCodec.ofMember(MusicDataResponsePacket::encode, MusicDataResponsePacket::decode);

    public static MusicDataResponsePacket decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        int version = buf.readInt();
        int eventCount = buf.readInt();
        if (eventCount < 0) {
            Mod.LOGGER.error("Invalid note count in MusicDataResponsePacket: {}", eventCount);
            return new MusicDataResponsePacket(id, version, List.of(), null);
        }
        List<NoteEvent> notes = new ArrayList<>(eventCount);
        for (int i = 0; i < eventCount; i++) {
            notes.add(NoteEvent.fromBuffer(buf));
        }
        try {
            return new MusicDataResponsePacket(id, version, notes, readVolumeMarkers(buf));
        } catch (IllegalArgumentException e) {
            Mod.LOGGER.error("Invalid MusicDataResponsePacket volume markers", e);
            return new MusicDataResponsePacket(id, version, notes, null);
        }
    }

    private static List<VolumeMarker> readVolumeMarkers(FriendlyByteBuf buf) {
        if (!buf.readBoolean()) {
            return null;
        }
        int markerCount = buf.readInt();
        if (markerCount < 0 || markerCount > Mod.MAX_VOLUME_MARKERS_IN_PACKET) {
            throw new IllegalArgumentException("markerCount=" + markerCount);
        }
        List<VolumeMarker> volumeMarkers = new ArrayList<>(markerCount);
        for (int i = 0; i < markerCount; i++) {
            volumeMarkers.add(VolumeMarker.fromBuffer(buf));
        }
        return volumeMarkers;
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        buf.writeInt(version);
        buf.writeInt(notes.size());
        for (NoteEvent event : notes) {
            event.encodeToBuffer(buf);
        }
        buf.writeBoolean(volumeMarkers != null);
        if (volumeMarkers != null) {
            buf.writeInt(volumeMarkers.size());
            for (VolumeMarker marker : volumeMarkers) {
                marker.encodeToBuffer(buf);
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
