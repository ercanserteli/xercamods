package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.packets.IPacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class MusicUpdatePacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "music_update");
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

    public static MusicUpdatePacket decode(FriendlyByteBuf buf) {
        MusicUpdatePacket result = new MusicUpdatePacket();
        try {
            FieldFlag flag = FieldFlag.fromInt(buf.readInt());
            result.availability = flag;
            if(flag.hasTitle) result.title = buf.readUtf(255);
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
            if(flag.hasId) result.id = buf.readUUID();
            if(flag.hasVersion) result.version = buf.readInt();
            if(flag.hasHlInterval) result.highlightInterval = buf.readByte();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(availability.toInt());
        if(availability.hasTitle) buf.writeUtf(title);
        if(availability.hasSigned) buf.writeBoolean(signed);
        if(availability.hasBps) buf.writeByte(bps);
        if(availability.hasVolume) buf.writeFloat(volume);
        if(availability.hasLength) buf.writeShort(lengthBeats);
        if(availability.hasNotes){
            if(notes != null) {
                buf.writeInt(notes.size());
                for (NoteEvent event : notes) {
                    event.encodeToBuffer(buf);
                }
            }
            else{
                buf.writeInt(0);
            }
        }
        if(availability.hasPrevIns) buf.writeByte(prevInstrument);
        if(availability.hasPrevInsLocked) buf.writeBoolean(prevInsLocked);
        if(availability.hasId) buf.writeUUID(id);
        if(availability.hasVersion) buf.writeInt(version);
        if(availability.hasHlInterval) buf.writeByte(highlightInterval);
        return buf;
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

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }

    public UUID getId() {
        return id;
    }

    @SuppressWarnings("unused")
    public void setId(UUID id) {
        this.id = id;
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
            return (hasNotes ? "Notes, " : "") + (hasLength ? "Length, " : "") + (hasBps ? "Bps, " : "")
                    + (hasVolume ? "Volume, " : "") + (hasSigned ? "Signed, " : "") + (hasTitle ? "Title, " : "")
                    + (hasPrevIns ? "PrevIns, " : "") + (hasPrevInsLocked ? "PrevInsLocked, " : "")
                    + (hasId ? "Id, " : "") + (hasVersion ? "Version, " : "") + (hasHlInterval ? "HL Interval" : "");
        }
    }
}