package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;

public class MusicUpdatePacket {
    private ArrayList<NoteEvent> notes;
    private short lengthBeats;
    private byte bps;
    private float volume;
    private boolean signed;
    private String title;
    private byte prevInstrument;
    private boolean prevInsLocked;
    private boolean messageIsValid;

    public MusicUpdatePacket(ArrayList<NoteEvent> notes, short lengthBeats, byte bps, float volume, boolean signed, String title, byte prevInstrument, boolean prevInsLocked) {
        this.notes = notes;
        this.lengthBeats = lengthBeats;
        this.bps = bps;
        this.volume = volume;
        this.signed = signed;
        this.title = title;
        this.prevInstrument = prevInstrument;
        this.prevInsLocked = prevInsLocked;
    }

    public MusicUpdatePacket() {
        this.messageIsValid = false;
    }

    public static MusicUpdatePacket decode(FriendlyByteBuf buf) {
        MusicUpdatePacket result = new MusicUpdatePacket();
        try {
            result.title = buf.readUtf(255);
            result.signed = buf.readBoolean();
            result.bps = buf.readByte();
            result.volume = buf.readFloat();
            result.lengthBeats = buf.readShort();
            int eventCount = buf.readInt();
            result.notes = new ArrayList<>(eventCount);
            for(int i=0; i<eventCount; i++){
                result.notes.add(NoteEvent.fromBuffer(buf));
            }
            result.prevInstrument = buf.readByte();
            result.prevInsLocked = buf.readBoolean();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicUpdatePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicUpdatePacket pkt, FriendlyByteBuf buf) {
        buf.writeUtf(pkt.title);
        buf.writeBoolean(pkt.signed);
        buf.writeByte(pkt.bps);
        buf.writeFloat(pkt.volume);
        buf.writeShort(pkt.lengthBeats);
        buf.writeInt(pkt.notes.size());
        for(NoteEvent event : pkt.notes){
            event.encodeToBuffer(buf);
        }
        buf.writeByte(pkt.prevInstrument);
        buf.writeBoolean(pkt.prevInsLocked);
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

}
