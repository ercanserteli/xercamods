package xerca.xercamusic.common.packets;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class ImportMusicSendPacket {
    private UUID uuid;
    private CompoundNBT tag;
    private ArrayList<NoteEvent> notes;
    private boolean messageIsValid;

    public ImportMusicSendPacket(CompoundNBT tag) throws NotesTooLargeException {
        this.tag = tag;
        if(this.tag.contains("id")) {
            this.uuid = tag.getUniqueId("id");
        }
        if(this.tag.contains("notes")) {
            this.notes = new ArrayList<>();
            NoteEvent.fillArrayFromNBT(this.notes, this.tag);
            this.tag.remove("notes");

            if(this.notes.size() > MAX_NOTES_IN_PACKET) {
                throw new NotesTooLargeException(notes, uuid);
            }
        }
    }

    public ImportMusicSendPacket() {
        this.messageIsValid = false;
    }

    public static void encode(ImportMusicSendPacket pkt, PacketBuffer buf) {
        if(pkt.notes != null) {
            buf.writeInt(pkt.notes.size());
            for(NoteEvent event : pkt.notes){
                event.encodeToBuffer(buf);
            }
        }
        else{
            buf.writeInt(0);
        }
        buf.writeCompoundTag(pkt.tag);
    }

    public static ImportMusicSendPacket decode(PacketBuffer buf) {
        ImportMusicSendPacket result = new ImportMusicSendPacket();
        try {
            int eventCount = buf.readInt();
            if(eventCount > 0) {
                result.notes = new ArrayList<>(eventCount);
                for (int i = 0; i < eventCount; i++) {
                    result.notes.add(NoteEvent.fromBuffer(buf));
                }
            }

            result.tag = buf.readCompoundTag();
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicSendPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public CompoundNBT getTag() {
        return tag;
    }

    public ArrayList<NoteEvent> getNotes() {
        return notes;
    }

    public void deleteNotes() {
        this.notes = null;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static class NotesTooLargeException extends Exception {
        public ArrayList<NoteEvent> notes;
        public UUID id;

        public NotesTooLargeException(ArrayList<NoteEvent> notes, UUID id) {
            this.notes = notes;
            this.id = id;
        }
    }
}
