package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.packets.serverbound.ImportMusicSendPacket.NotesTooLargeException;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.Mod.MAX_NOTES_IN_PACKET;

public record MusicUpdatePacket(FieldFlag availability, ArrayList<NoteEvent> notes, short lengthBeats, byte bps, float volume, boolean signed, String title, byte prevInstrument, boolean prevInsLocked, UUID id, int version, byte highlightInterval) implements CustomPacketPayload  {
    public static final Type<MusicUpdatePacket> PACKET_ID = new Type<>(Mod.id("music_update"));
    public static final StreamCodec<FriendlyByteBuf, MusicUpdatePacket> PACKET_CODEC = StreamCodec.ofMember(MusicUpdatePacket::encode, MusicUpdatePacket::decode);

    public static MusicUpdatePacket create(FieldFlag availability, ArrayList<NoteEvent> notes, short lengthBeats, byte bps, float volume, boolean signed, String title, byte prevInstrument, boolean prevInsLocked, UUID id, int version, byte highlightInterval) throws NotesTooLargeException {
        if (notes != null && notes.size() > MAX_NOTES_IN_PACKET) {
            throw new NotesTooLargeException(notes, id);
        }
        return new MusicUpdatePacket(availability, notes, lengthBeats, bps, volume, signed, title, prevInstrument, prevInsLocked, id, version, highlightInterval);
    }

    public static MusicUpdatePacket decode(FriendlyByteBuf buf) {
        try {
            FieldFlag flag = FieldFlag.fromInt(buf.readInt());

            ArrayList<NoteEvent> notes = null;
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

            return MusicUpdatePacket.create(
                    flag,
                    notes,
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
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        } catch (NotesTooLargeException e) {
            System.err.println("NotesTooLargeException while reading MusicUpdatePacket: " + e);
            return null;
        }
    }


    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
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

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
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