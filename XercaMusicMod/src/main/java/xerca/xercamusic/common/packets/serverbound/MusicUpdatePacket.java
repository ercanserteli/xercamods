package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.VolumeMarker;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket.NotesTooLargeException;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;

public record MusicUpdatePacket(FieldFlag availability, ArrayList<NoteEvent> notes, ArrayList<VolumeMarker> volumeMarkers, short lengthBeats, byte bps,
                                float volume, boolean signed, String title, byte prevInstrument, boolean prevInsLocked,
                                UUID id, int version, byte highlightInterval) implements CustomPacketPayload {
    public static final Type<MusicUpdatePacket> PACKET_ID = new Type<>(Mod.id("music_update"));
    public static final StreamCodec<FriendlyByteBuf, MusicUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(MusicUpdatePacket::encode, MusicUpdatePacket::decode);

    public static MusicUpdatePacket createEmpty() {
        return new MusicUpdatePacket(new FieldFlag(), null, null, (short) 0, (byte) 0, 0.0f, false, null, (byte) 0, false, null, 0, (byte) 0);
    }

    public static MusicUpdatePacket create(FieldFlag availability, ArrayList<NoteEvent> notes, ArrayList<VolumeMarker> volumeMarkers, short lengthBeats, byte bps, float volume, boolean signed, String title, byte prevInstrument, boolean prevInsLocked, UUID id, int version, byte highlightInterval) throws NotesTooLargeException {
        if (notes != null && notes.size() > MAX_NOTES_IN_PACKET) {
            throw new NotesTooLargeException(notes, id);
        }
        return new MusicUpdatePacket(availability, notes, volumeMarkers, lengthBeats, bps, volume, signed, title, prevInstrument, prevInsLocked, id, version, highlightInterval);
    }

    public static MusicUpdatePacket decode(FriendlyByteBuf buf) {
        try {
            FieldFlag flag = FieldFlag.fromInt(buf.readInt());

            ArrayList<NoteEvent> notes = null;
            ArrayList<VolumeMarker> volumeMarkers = null;
            short lengthBeats = 0;
            byte bps = 0;
            float volume = 0.0f;
            boolean signed = false;
            String title = null;
            byte prevInstrument = 0;
            boolean prevInsLocked = false;
            UUID id = null;
            int version = 0;
            byte highlightInterval = 0;

            if (flag.hasTitle) title = buf.readUtf(255);
            if (flag.hasSigned) signed = buf.readBoolean();
            if (flag.hasBps) bps = buf.readByte();
            if (flag.hasVolume) volume = buf.readFloat();
            if (flag.hasLength) lengthBeats = buf.readShort();
            if (flag.hasNotes) {
                int eventCount = buf.readInt();
                if (eventCount < 0 || eventCount > MAX_NOTES_IN_PACKET) {
                    throw new IllegalArgumentException("eventCount=" + eventCount);
                }
                if (eventCount != 0) {  // Notes may have been sent in parts beforehand
                    notes = new ArrayList<>(eventCount);
                    for (int i = 0; i < eventCount; i++) {
                        notes.add(NoteEvent.fromBuffer(buf));
                    }
                }
            }
            if (flag.hasPrevIns) prevInstrument = buf.readByte();
            if (flag.hasPrevInsLocked) prevInsLocked = buf.readBoolean();
            if (flag.hasId) id = buf.readUUID();
            if (flag.hasVersion) version = buf.readInt();
            if (flag.hasHlInterval) highlightInterval = buf.readByte();
            if (flag.hasVolumeMarkers) {
                volumeMarkers = readVolumeMarkers(buf);
            }

            return MusicUpdatePacket.create(
                    flag,
                    notes,
                    volumeMarkers,
                    lengthBeats,
                    bps,
                    volume,
                    signed,
                    title,
                    prevInstrument,
                    prevInsLocked,
                    id,
                    version,
                    highlightInterval
            );
        } catch (IllegalArgumentException | NotesTooLargeException e) {
            Mod.LOGGER.error("Invalid MusicUpdatePacket", e);
            return createEmpty();
        }
    }

    private static ArrayList<VolumeMarker> readVolumeMarkers(FriendlyByteBuf buf) {
        int markerCount = buf.readInt();
        if (markerCount < 0 || markerCount > Mod.MAX_VOLUME_MARKERS_IN_PACKET) {
            throw new IllegalArgumentException("markerCount=" + markerCount);
        }
        if (markerCount == 0) {
            return null;
        }
        ArrayList<VolumeMarker> volumeMarkers = new ArrayList<>(markerCount);
        for (int i = 0; i < markerCount; i++) {
            volumeMarkers.add(VolumeMarker.fromBuffer(buf));
        }
        return volumeMarkers;
    }


    public void encode(FriendlyByteBuf buf) {
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
        if (availability.hasId) buf.writeUUID(id);
        if (availability.hasVersion) buf.writeInt(version);
        if (availability.hasHlInterval) buf.writeByte(highlightInterval);
        if (availability.hasVolumeMarkers) {
            if (volumeMarkers != null) {
                buf.writeInt(volumeMarkers.size());
                for (VolumeMarker marker : volumeMarkers) {
                    marker.encodeToBuffer(buf);
                }
            } else {
                buf.writeInt(0);
            }
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
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
                         boolean hasVersion, boolean hasHlInterval, boolean hasVolumeMarkers) {
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
            this.hasVolumeMarkers = hasVolumeMarkers;
        }

        public FieldFlag() {
        }

        public static FieldFlag fromInt(int packed) {
            return new FieldFlag(
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
                    (packed & HL_INTERVAL_FLAG) != 0,
                    (packed & VOLUME_MARKERS_FLAG) != 0
            );
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

        public boolean hasAny() {
            return hasNotes || hasLength || hasBps || hasVolume || hasSigned || hasTitle || hasPrevIns ||
                    hasPrevInsLocked || hasId || hasVersion || hasHlInterval || hasVolumeMarkers;
        }

        @Override
        public String toString() {
            return (hasNotes ? "Notes, " : "") + (hasLength ? "Length, " : "") + (hasBps ? "Bps, " : "")
                    + (hasVolume ? "Volume, " : "") + (hasSigned ? "Signed, " : "") + (hasTitle ? "Title, " : "")
                    + (hasPrevIns ? "PrevIns, " : "") + (hasPrevInsLocked ? "PrevInsLocked, " : "")
                    + (hasId ? "Id, " : "") + (hasVersion ? "Version, " : "") + (hasHlInterval ? "HL Interval, " : "")
                    + (hasVolumeMarkers ? "VolumeMarkers" : "");
        }
    }
}
