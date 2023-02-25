package xerca.xercamusic.common.packets;

import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class MusicUpdatePacket {
    private FieldFlag availability;
    private ArrayList<NoteEvent> notes;
    private short lengthBeats;
    private byte bps;
    private float volume;
    private boolean signed;
    private String title;
    private byte prevInstrument;
    private boolean prevInsLocked;
    private UUID id;
    private int version;
    private byte highlightInterval;
    private boolean messageIsValid;

    public MusicUpdatePacket(FieldFlag availability, ArrayList<NoteEvent> notes, short lengthBeats, byte bps, float volume, boolean signed,
                             String title, byte prevInstrument, boolean prevInsLocked, UUID id, int version, byte highlightInterval) throws ImportMusicSendPacket.NotesTooLargeException {
        this.availability = availability;
        this.lengthBeats = lengthBeats;
        this.bps = bps;
        this.volume = volume;
        this.signed = signed;
        this.title = title;
        this.prevInstrument = prevInstrument;
        this.prevInsLocked = prevInsLocked;
        this.id = id;
        this.version = version;
        this.highlightInterval = highlightInterval;
        this.notes = notes;
        if(availability.hasNotes && this.notes != null && this.notes.size() > MAX_NOTES_IN_PACKET) {
            throw new ImportMusicSendPacket.NotesTooLargeException(notes, id);
        }
    }

    public MusicUpdatePacket() {
        this.messageIsValid = false;
    }

    public static MusicUpdatePacket decode(PacketBuffer buf) {
        MusicUpdatePacket result = new MusicUpdatePacket();
        try {
            FieldFlag flag = FieldFlag.fromInt(buf.readInt());
            result.availability = flag;
            if(flag.hasTitle) result.title = buf.readString(255);
            if(flag.hasSigned) result.signed = buf.readBoolean();
            if(flag.hasBps) result.bps = buf.readByte();
            if(flag.hasVolume) result.volume = buf.readFloat();
            if(flag.hasLength) result.lengthBeats = buf.readShort();
            if(flag.hasNotes){
                int eventCount = buf.readInt();
                if(eventCount != 0) {  // if this is false, notes may have been sent in parts beforehand
                    result.notes = new ArrayList<>(eventCount);
                    for (int i = 0; i < eventCount; i++) {
                        result.notes.add(NoteEvent.fromBuffer(buf));
                    }
                }
            }
            if(flag.hasPrevIns) result.prevInstrument = buf.readByte();
            if(flag.hasPrevInsLocked) result.prevInsLocked = buf.readBoolean();
            if(flag.hasId) result.id = buf.readUniqueId();
            if(flag.hasVersion) result.version = buf.readInt();
            if(flag.hasHlInterval) result.highlightInterval = buf.readByte();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicUpdatePacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.availability.toInt());
        if(pkt.availability.hasTitle) buf.writeString(pkt.title);
        if(pkt.availability.hasSigned) buf.writeBoolean(pkt.signed);
        if(pkt.availability.hasBps) buf.writeByte(pkt.bps);
        if(pkt.availability.hasVolume) buf.writeFloat(pkt.volume);
        if(pkt.availability.hasLength) buf.writeShort(pkt.lengthBeats);
        if(pkt.availability.hasNotes){
            if(pkt.notes != null) {
                buf.writeInt(pkt.notes.size());
                for (NoteEvent event : pkt.notes) {
                    event.encodeToBuffer(buf);
                }
            }
            else{
                buf.writeInt(0);
            }
        }
        if(pkt.availability.hasPrevIns) buf.writeByte(pkt.prevInstrument);
        if(pkt.availability.hasPrevInsLocked) buf.writeBoolean(pkt.prevInsLocked);
        if(pkt.availability.hasId) buf.writeUniqueId(pkt.id);
        if(pkt.availability.hasVersion) buf.writeInt(pkt.version);
        if(pkt.availability.hasHlInterval) buf.writeByte(pkt.highlightInterval);
    }

    public ArrayList<NoteEvent> getNotes() {
        return notes;
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

    public boolean isMessageValid() {
        return messageIsValid;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public byte getHighlightInterval() {
        return highlightInterval;
    }

    public void setHighlightInterval(byte highlightInterval) {
        this.highlightInterval = highlightInterval;
    }

    public FieldFlag getAvailability() {
        return availability;
    }

    public void setAvailability(FieldFlag availability) {
        this.availability = availability;
    }

    public static class FieldFlag {
        private static final int notesFlag = 1;
        private static final int lengthFlag = 1 << 1;
        private static final int bpsFlag = 1 << 2;
        private static final int volumeFlag = 1 << 3;
        private static final int signedFlag = 1 << 4;
        private static final int titleFlag = 1 << 5;
        private static final int prevInsFlag = 1 << 6;
        private static final int prevInsLockedFlag = 1 << 7;
        private static final int idFlag = 1 << 8;
        private static final int versionFlag = 1 << 9;
        private static final int hlIntervalFlag = 1 << 10;

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

        public FieldFlag(boolean hasNotes, boolean hasLength, boolean hasBps, boolean hasVolume, boolean hasSigned,
                         boolean hasTitle, boolean hasPrevIns, boolean hasPrevInsLocked, boolean hasId,
                         boolean hasVersion, boolean hasHlInterval){
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

        public FieldFlag(){
        }

        public int toInt(){
            return (hasNotes ? notesFlag : 0) |
                   (hasLength ? lengthFlag : 0) |
                   (hasBps ? bpsFlag : 0) |
                   (hasVolume ? volumeFlag : 0) |
                   (hasSigned ? signedFlag : 0) |
                   (hasTitle ? titleFlag : 0) |
                   (hasPrevIns ? prevInsFlag : 0) |
                   (hasPrevInsLocked ? prevInsLockedFlag : 0) |
                   (hasId ? idFlag : 0) |
                   (hasVersion ? versionFlag : 0) |
                   (hasHlInterval ? hlIntervalFlag : 0);
        }

        static public FieldFlag fromInt(int packed){
            return new FieldFlag(
                    (packed & notesFlag) != 0,
                    (packed & lengthFlag) != 0,
                    (packed & bpsFlag) != 0,
                    (packed & volumeFlag) != 0,
                    (packed & signedFlag) != 0,
                    (packed & titleFlag) != 0,
                    (packed & prevInsFlag) != 0,
                    (packed & prevInsLockedFlag) != 0,
                    (packed & idFlag) != 0,
                    (packed & versionFlag) != 0,
                    (packed & hlIntervalFlag) != 0
                    );
        }

        public boolean hasAny() {
            return hasNotes || hasLength || hasBps || hasVolume || hasSigned || hasTitle || hasPrevIns ||
                    hasPrevInsLocked || hasId || hasVersion || hasHlInterval;
        }

        public String toString() {
            return "" + (hasNotes ? "Notes, " : "") + (hasLength ? "Length, " : "") + (hasBps ? "Bps, " : "")
                    + (hasVolume ? "Volume, " : "") + (hasSigned ? "Signed, " : "") + (hasTitle ? "Title, " : "")
                    + (hasPrevIns ? "PrevIns, " : "") + (hasPrevInsLocked ? "PrevInsLocked, " : "")
                    + (hasId ? "Id, " : "") + (hasVersion ? "Version, " : "") + (hasHlInterval ? "HL Interval" : "");
        }
    }
}