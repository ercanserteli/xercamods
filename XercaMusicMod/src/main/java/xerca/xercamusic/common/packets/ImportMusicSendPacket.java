package xerca.xercamusic.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.NoteEvent;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;

public class ImportMusicSendPacket {
    private UUID uuid;
    private CompoundTag tag;
    private ArrayList<NoteEvent> notes;
    private boolean messageIsValid;

    public ImportMusicSendPacket(CompoundTag tag) throws NotesTooLargeException {
        this.tag = tag;
        if(this.tag.contains("id")) {
            this.uuid = tag.getUUID("id");
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

    public static void encode(ImportMusicSendPacket pkt, FriendlyByteBuf buf) {
        if(pkt.notes != null) {
            buf.writeInt(pkt.notes.size());
            for(NoteEvent event : pkt.notes){
                event.encodeToBuffer(buf);
            }
        }
        else{
            buf.writeInt(0);
        }
        buf.writeNbt(pkt.tag);
    }

    public static ImportMusicSendPacket decode(FriendlyByteBuf buf) {
        ImportMusicSendPacket result = new ImportMusicSendPacket();
        try {
            int eventCount = buf.readInt();
            if(eventCount > 0) {
                result.notes = new ArrayList<>(eventCount);
                for (int i = 0; i < eventCount; i++) {
                    result.notes.add(NoteEvent.fromBuffer(buf));
                }
            }

            result.tag = buf.readNbt();

        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ImportMusicSendPacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public CompoundTag getTag() {
        return tag;
    }

    public ArrayList<NoteEvent> getNotes() {
        return notes;
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

    public UUID getUuid() {
        return uuid;
    }

    public static class NotesTooLargeException extends Exception {
        public final ArrayList<NoteEvent> notes;
        public final UUID id;

        public NotesTooLargeException(ArrayList<NoteEvent> notes, UUID id) {
            this.notes = notes;
            this.id = id;
        }
    }
}
