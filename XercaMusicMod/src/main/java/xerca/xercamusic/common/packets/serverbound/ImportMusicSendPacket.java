package xerca.xercamusic.common.packets.serverbound;

import io.netty.buffer.Unpooled;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.NoteEvent;
import xerca.xercamusic.common.packets.IPacket;

import java.util.ArrayList;
import java.util.UUID;

import static xerca.xercamusic.common.XercaMusic.MAX_NOTES_IN_PACKET;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_ID;
import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_NOTES;

@SuppressWarnings("unused")
public class ImportMusicSendPacket implements IPacket {
    private UUID uuid;
    private CompoundTag tag;
    private ArrayList<NoteEvent> notes;
    private boolean messageIsValid;

    public ImportMusicSendPacket(CompoundTag tag) throws NotesTooLargeException {
        this.tag = tag;
        if (this.tag.contains(KEY_ID)) {
            this.uuid = tag.getUUID(KEY_ID);
        }
        if (this.tag.contains(KEY_NOTES)) {
            this.notes = new ArrayList<>();
            NoteEvent.fillArrayFromNBT(this.notes, this.tag);
            this.tag.remove(KEY_NOTES);

            if (this.notes.size() > MAX_NOTES_IN_PACKET) {
                throw new NotesTooLargeException(notes, uuid);
            }
        }
    }

    public ImportMusicSendPacket() {
        this.messageIsValid = false;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        if (notes != null) {
            buf.writeInt(notes.size());
            for (NoteEvent event : notes) {
                event.encodeToBuffer(buf);
            }
        } else {
            buf.writeInt(0);
        }
        buf.writeNbt(tag);
        return buf;
    }

    public static ImportMusicSendPacket decode(FriendlyByteBuf buf) {
        ImportMusicSendPacket result = new ImportMusicSendPacket();
        try {
            int eventCount = buf.readInt();
            if (eventCount < 0 || eventCount > MAX_NOTES_IN_PACKET) {
                throw new IndexOutOfBoundsException("Invalid eventCount: " + eventCount);
            }
            if (eventCount > 0) {
                result.notes = new ArrayList<>(eventCount);
                for (int i = 0; i < eventCount; i++) {
                    result.notes.add(NoteEvent.fromBuffer(buf));
                }
            }

            result.tag = buf.readNbt();

        } catch (RuntimeException ioe) {
            XercaMusic.LOGGER.error("Exception while reading ImportMusicSendPacket: {}", String.valueOf(ioe));
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
        public final ArrayList<NoteEvent> notes;
        public final UUID id;

        public NotesTooLargeException(ArrayList<NoteEvent> notes, UUID id) {
            this.notes = notes;
            this.id = id;
        }
    }
}
