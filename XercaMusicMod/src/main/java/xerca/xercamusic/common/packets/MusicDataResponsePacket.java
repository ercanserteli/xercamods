package xerca.xercamusic.common.packets;

import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.XercaMusic;

import java.util.ArrayList;
import java.util.UUID;

public class MusicDataResponsePacket {
    private UUID id;
    private int version;
    private ArrayList<NoteEvent> notes;
    private boolean messageIsValid;

    public MusicDataResponsePacket(UUID id, int version, ArrayList<NoteEvent> notes) {
        this.id = id;
        this.version = version;
        this.notes = notes;
    }

    public MusicDataResponsePacket() {
        this.messageIsValid = false;
    }

    public static MusicDataResponsePacket decode(PacketBuffer buf) {
        MusicDataResponsePacket result = new MusicDataResponsePacket();
        try {
            result.id = buf.readUniqueId();
            result.version = buf.readInt();
            int eventCount = buf.readInt();
            result.notes = new ArrayList<>(eventCount);
            for(int i=0; i<eventCount; i++){
                result.notes.add(NoteEvent.fromBuffer(buf));
            }
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading MusicDataRequestPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(MusicDataResponsePacket pkt, PacketBuffer buf) {
        buf.writeUniqueId(pkt.getId());
        buf.writeInt(pkt.getVersion());
        buf.writeInt(pkt.notes.size());
        for(NoteEvent event : pkt.notes){
            event.encodeToBuffer(buf);
        }
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

    public ArrayList<NoteEvent> getNotes() {
        return notes;
    }

    public void setNotes(ArrayList<NoteEvent> notes) {
        this.notes = notes;
    }
}
