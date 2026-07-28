package xerca.xercamusic.common.packets.serverbound;

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

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;

public class MusicUpdatePacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(Mod.MODID, "music_update");
    private FieldFlag availability;
    private ArrayList<NoteEvent> notes;
    private List<VolumeMarker> volumeMarkers;
    private short lengthBeats;
    private byte bps;
    private float volume;
    private boolean signed;
    private String title;
    private byte prevInstrument;
    private boolean prevInsLocked;
    private UUID musicId;
    private int version;
    private byte highlightInterval;
    private boolean messageIsValid;

    public MusicUpdatePacket(FieldFlag availability, ArrayList<NoteEvent> notes, short lengthBeats, byte bps, float volume, boolean signed,
                             String title, byte prevInstrument, boolean prevInsLocked, UUID musicId, int version, byte highlightInterval) throws ImportMusicSendPacket.NotesTooLargeException {
        this(availability, notes, null, lengthBeats, bps, volume, signed, title, prevInstrument, prevInsLocked, musicId, version, highlightInterval);
    }

    private MusicUpdatePacket(FieldFlag availability, ArrayList<NoteEvent> notes, List<VolumeMarker> volumeMarkers,
                              short lengthBeats, byte bps, float volume, boolean signed, String title,
                              byte prevInstrument, boolean prevInsLocked, UUID musicId, int version,
                              byte highlightInterval) throws ImportMusicSendPacket.NotesTooLargeException {
        this.availability = availability;
        this.lengthBeats = lengthBeats;
        this.bps = bps;
        this.volume = volume;
        this.signed = signed;
        this.title = title;
        this.prevInstrument = prevInstrument;
        this.prevInsLocked = prevInsLocked;
        this.musicId = musicId;
        this.version = version;
        this.highlightInterval = highlightInterval;
        this.notes = notes;
        this.volumeMarkers = volumeMarkers;
        if (availability.hasNotes && this.notes != null && this.notes.size() > MAX_NOTES_IN_PACKET) {
            throw new ImportMusicSendPacket.NotesTooLargeException(notes, musicId);
        }
    }

    public static MusicUpdatePacket create(FieldFlag availability, ArrayList<NoteEvent> notes,
                                            List<VolumeMarker> volumeMarkers, short lengthBeats, byte bps,
                                            float volume, boolean signed, String title, byte prevInstrument,
                                            boolean prevInsLocked, UUID musicId, int version,
                                            byte highlightInterval) throws ImportMusicSendPacket.NotesTooLargeException {
        return new MusicUpdatePacket(availability, notes, volumeMarkers, lengthBeats, bps, volume, signed,
                title, prevInstrument, prevInsLocked, musicId, version, highlightInterval);
    }

    public MusicUpdatePacket() {
        this.messageIsValid = false;
    }

    public static MusicUpdatePacket decode(FriendlyByteBuf buf) {
        MusicUpdatePacket result = new MusicUpdatePacket();
        try {
            FieldFlag flag = FieldFlag.fromInt(buf.readInt());
            result.availability = flag;
            if (flag.hasTitle) result.title = buf.readUtf(255);
            if (flag.hasSigned) result.signed = buf.readBoolean();
            if (flag.hasBps) result.bps = buf.readByte();
            if (flag.hasVolume) result.volume = buf.readFloat();
            if (flag.hasLength) result.lengthBeats = buf.readShort();
            if (flag.hasNotes) {
                int eventCount = buf.readInt();
                if (eventCount < 0 || eventCount > MAX_NOTES_IN_PACKET) {
                    throw new IndexOutOfBoundsException("Invalid eventCount: " + eventCount);
                }
                if (eventCount > 0) {  // if this is false, notes may have been sent in parts beforehand
                    result.notes = new ArrayList<>(eventCount);
                    for (int i = 0; i < eventCount; i++) {
                        result.notes.add(NoteEvent.fromBuffer(buf));
                    }
                }
            }
            if (flag.hasPrevIns) result.prevInstrument = buf.readByte();
            if (flag.hasPrevInsLocked) result.prevInsLocked = buf.readBoolean();
            if (flag.hasId) result.musicId = buf.readUUID();
            if (flag.hasVersion) result.version = buf.readInt();
            if (flag.hasHlInterval) result.highlightInterval = buf.readByte();
            if (flag.hasVolumeMarkers) {
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
            Mod.LOGGER.error("Exception while reading MusicUpdatePacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(availability.toInt());
        if (availability.hasTitle) buf.writeUtf(title);
        if (availability.hasSigned) buf.writeBoolean(signed);
        if (availability.hasBps) buf.writeByte(bps);
        if (availability.hasVolume) buf.writeFloat(volume);
        if (availability.hasLength) buf.writeShort(lengthBeats);
        if (availability.hasNotes) {
            if (notes != null) {
                buf.writeInt(notes.size());
                for (NoteEvent event : notes) {
                    event.encodeToBuffer(buf);
                }
            } else {
                buf.writeInt(0);
            }
        }
        if (availability.hasPrevIns) buf.writeByte(prevInstrument);
        if (availability.hasPrevInsLocked) buf.writeBoolean(prevInsLocked);
        if (availability.hasId) buf.writeUUID(musicId);
        if (availability.hasVersion) buf.writeInt(version);
        if (availability.hasHlInterval) buf.writeByte(highlightInterval);
        if (availability.hasVolumeMarkers) {
            int markerCount = volumeMarkers == null ? 0 : volumeMarkers.size();
            buf.writeInt(markerCount);
            if (volumeMarkers != null) {
                for (VolumeMarker marker : volumeMarkers) {
                    marker.encodeToBuffer(buf);
                }
            }
        }
        return buf;
    }

    public ArrayList<NoteEvent> getNotes() {
        return notes;
    }

    public List<VolumeMarker> getVolumeMarkers() {
        return volumeMarkers;
    }

    public short getLengthBeats() {
        return lengthBeats;
    }

    public byte getBps() {
        return bps;
    }

    public float getVolume() {
        return volume;
    }

    public boolean getSigned() {
        return signed;
    }

    public String getTitle() {
        return title;
    }

    public byte getPrevInstrument() {
        return prevInstrument;
    }

    public boolean getPrevInsLocked() {
        return prevInsLocked;
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

    public byte getHighlightInterval() {
        return highlightInterval;
    }

    @SuppressWarnings("unused")
    public void setHighlightInterval(byte highlightInterval) {
        this.highlightInterval = highlightInterval;
    }

    public FieldFlag getAvailability() {
        return availability;
    }

    @SuppressWarnings("unused")
    public void setAvailability(FieldFlag availability) {
        this.availability = availability;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    public static class FieldFlag {
        private static final int NOTES_FLAG = 1;
        private static final int LENGTH_FLAG = 1 << 1;
        private static final int BPS_FLAG = 1 << 2;
        private static final int VOLUME_FLAG = 1 << 3;
        private static final int SIGNED_FLAG = 1 << 4;
        private static final int TITLE_FLAG = 1 << 5;
        private static final int PREV_INS_FLAG = 1 << 6;
        private static final int PREV_INS_LOCKED_FLAG = 1 << 7;
        private static final int ID_FLAG = 1 << 8;
        private static final int VERSION_FLAG = 1 << 9;
        private static final int HL_INTERVAL_FLAG = 1 << 10;
        private static final int VOLUME_MARKERS_FLAG = 1 << 11;

        public boolean hasNotes;
        public boolean hasLength;
        public boolean hasBps;
        public boolean hasVolume;
        public boolean hasSigned;
        public boolean hasTitle;
        public boolean hasPrevIns;
        public boolean hasPrevInsLocked;
        public boolean hasId;
        public boolean hasVersion;
        public boolean hasHlInterval;
        public boolean hasVolumeMarkers;

        public FieldFlag(boolean hasNotes, boolean hasLength, boolean hasBps, boolean hasVolume, boolean hasSigned,
                         boolean hasTitle, boolean hasPrevIns, boolean hasPrevInsLocked, boolean hasId,
                         boolean hasVersion, boolean hasHlInterval) {
            this.hasNotes = hasNotes;
            this.hasLength = hasLength;
            this.hasBps = hasBps;
            this.hasVolume = hasVolume;
            this.hasSigned = hasSigned;
            this.hasTitle = hasTitle;
            this.hasPrevIns = hasPrevIns;
            this.hasPrevInsLocked = hasPrevInsLocked;
            this.hasId = hasId;
            this.hasVersion = hasVersion;
            this.hasHlInterval = hasHlInterval;
        }

        public FieldFlag() {
        }

        public int toInt() {
            return (hasNotes ? NOTES_FLAG : 0) |
                    (hasLength ? LENGTH_FLAG : 0) |
                    (hasBps ? BPS_FLAG : 0) |
                    (hasVolume ? VOLUME_FLAG : 0) |
                    (hasSigned ? SIGNED_FLAG : 0) |
                    (hasTitle ? TITLE_FLAG : 0) |
                    (hasPrevIns ? PREV_INS_FLAG : 0) |
                    (hasPrevInsLocked ? PREV_INS_LOCKED_FLAG : 0) |
                    (hasId ? ID_FLAG : 0) |
                    (hasVersion ? VERSION_FLAG : 0) |
                    (hasHlInterval ? HL_INTERVAL_FLAG : 0) |
                    (hasVolumeMarkers ? VOLUME_MARKERS_FLAG : 0);
        }

        public static FieldFlag fromInt(int packed) {
            FieldFlag flag = new FieldFlag(
                    (packed & NOTES_FLAG) != 0,
                    (packed & LENGTH_FLAG) != 0,
                    (packed & BPS_FLAG) != 0,
                    (packed & VOLUME_FLAG) != 0,
                    (packed & SIGNED_FLAG) != 0,
                    (packed & TITLE_FLAG) != 0,
                    (packed & PREV_INS_FLAG) != 0,
                    (packed & PREV_INS_LOCKED_FLAG) != 0,
                    (packed & ID_FLAG) != 0,
                    (packed & VERSION_FLAG) != 0,
                    (packed & HL_INTERVAL_FLAG) != 0
            );
            flag.hasVolumeMarkers = (packed & VOLUME_MARKERS_FLAG) != 0;
            return flag;
        }

        public boolean hasAny() {
            return hasNotes || hasLength || hasBps || hasVolume || hasSigned || hasTitle || hasPrevIns ||
                    hasPrevInsLocked || hasId || hasVersion || hasHlInterval || hasVolumeMarkers;
        }

        public String toString() {
            return (hasNotes ? "Notes, " : "") + (hasLength ? "Length, " : "") + (hasBps ? "Bps, " : "")
                    + (hasVolume ? "Volume, " : "") + (hasSigned ? "Signed, " : "") + (hasTitle ? "Title, " : "")
                    + (hasPrevIns ? "PrevIns, " : "") + (hasPrevInsLocked ? "PrevInsLocked, " : "")
                    + (hasId ? "Id, " : "") + (hasVersion ? "Version, " : "") + (hasHlInterval ? "HL Interval, " : "")
                    + (hasVolumeMarkers ? "Volume Markers" : "");
        }
    }
}
