package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.packets.IPacket;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MusicDataResponsePacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(Mod.MODID, "music_data_response");
    private UUID musicId;
    private int version;
    private List<NoteEvent> notes;
    private List<VolumeMarker> volumeMarkers;
    private boolean messageIsValid;

    public MusicDataResponsePacket(UUID musicId, int version, List<NoteEvent> notes) {
        this(musicId, version, notes, null);
    }

    public MusicDataResponsePacket(UUID musicId, int version, List<NoteEvent> notes, List<VolumeMarker> volumeMarkers) {
        this.musicId = musicId;
        this.version = version;
        this.notes = notes;
        this.volumeMarkers = volumeMarkers;
    }

    public MusicDataResponsePacket() {
        this.messageIsValid = false;
    }

    public static MusicDataResponsePacket decode(FriendlyByteBuf buf) {
        MusicDataResponsePacket result = new MusicDataResponsePacket();
        try {
            result.musicId = buf.readUUID();
            result.version = buf.readInt();
            int eventCount = buf.readInt();
            if (eventCount < 0) {
                throw new IndexOutOfBoundsException("Invalid eventCount: " + eventCount);
            }
            result.notes = new ArrayList<>(eventCount);
            for (int i = 0; i < eventCount; i++) {
                result.notes.add(NoteEvent.fromBuffer(buf));
            }
            if (buf.readBoolean()) {
                int markerCount = buf.readInt();
                if (markerCount < 0 || markerCount > Mod.MAX_VOLUME_MARKERS_IN_PACKET) {
                    throw new IndexOutOfBoundsException("Invalid markerCount: " + markerCount);
                }
                result.volumeMarkers = new ArrayList<>(markerCount);
                for (int i = 0; i < markerCount; i++) {
                    result.volumeMarkers.add(VolumeMarker.fromBuffer(buf));
                }
            }
        } catch (RuntimeException ioe) {
            Mod.LOGGER.error("Exception while reading MusicDataRequestPacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeUUID(getMusicId());
        buf.writeInt(getVersion());
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
        return buf;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }


    public UUID getMusicId() {
        return musicId;
    }

    @SuppressWarnings("unused")
    public void setMusicId(UUID musicId) {
        this.musicId = musicId;
    }

    public int getVersion() {
        return version;
    }

    @SuppressWarnings("unused")
    public void setVersion(int version) {
        this.version = version;
    }

    public List<NoteEvent> getNotes() {
        return notes;
    }

    public List<VolumeMarker> getVolumeMarkers() {
        return volumeMarkers;
    }

    @SuppressWarnings("unused")
    public void setNotes(List<NoteEvent> notes) {
        this.notes = notes;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
