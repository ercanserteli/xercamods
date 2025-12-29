package xerca.xercamusic.common;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.item.IItemInstrument;

import java.util.Comparator;
import java.util.List;

import static xerca.xercamusic.common.item.ItemMusicSheet.KEY_NOTES;

public class NoteEvent {
    public byte note;
    public short time;
    public byte volume;
    public byte length;

    public NoteEvent(byte note, short time, byte volume, byte length) {
        this.note = note;
        this.time = time;
        this.volume = volume;
        this.length = length;
    }

    public NoteEvent() {
    }

    public NoteEvent(NoteEvent noteEvent) {
        this.note = noteEvent.note;
        this.time = noteEvent.time;
        this.volume = noteEvent.volume;
        this.length = noteEvent.length;
    }

    public static NoteEvent fromNBT(CompoundTag tag) {
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.deserializeNBT(tag);
        return noteEvent;
    }

    public static NoteEvent fromBuffer(FriendlyByteBuf buf) {
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.decodeFromBuffer(buf);
        return noteEvent;
    }

    public static void fillArrayFromNBT(List<NoteEvent> noteEvents, CompoundTag tag) {
        ListTag notesTag = tag.getList(KEY_NOTES, Tag.TAG_COMPOUND);
        for (int i = 0; i < notesTag.size(); i++) {
            noteEvents.add(NoteEvent.fromNBT(notesTag.getCompound(i)));
        }
        sortNotes(noteEvents);
        removeDuplicates(noteEvents);
    }

    public static void sortNotes(List<NoteEvent> notes) {
        notes.sort(Comparator.comparingInt(NoteEvent::startTime));
    }

    public static void removeDuplicates(List<NoteEvent> notes) {
        if (notes.isEmpty()) {
            return;
        }

        short currentTime = notes.get(0).time;
        long seenLo = 0L; // 0..63
        long seenHi = 0L; // 64..127

        int i = 0;
        while (i < notes.size()) {  // NOSONAR
            NoteEvent e = notes.get(i);

            if (e.note < IItemInstrument.MIN_NOTE || e.note > IItemInstrument.MAX_NOTE) {
                // invalid note
                notes.remove(i);
                continue;
            }

            // new time group -> reset bitsets
            if (e.time != currentTime) {
                currentTime = e.time;
                seenLo = 0L;
                seenHi = 0L;
            }

            int note = e.note & 0xFF;
            if (note < 64) {
                long bit = 1L << note;
                if ((seenLo & bit) != 0L) {
                    // duplicate found
                    notes.remove(i);
                    continue;
                }
                seenLo |= bit;
            } else {
                long bit = 1L << (note - 64);
                if ((seenHi & bit) != 0L) {
                    // duplicate found
                    notes.remove(i);
                    continue;
                }
                seenHi |= bit;
            }

            i++;
        }
    }

    public static void fillNBTFromArray(List<NoteEvent> noteEvents, CompoundTag tag) {
        ListTag noteList = new ListTag();
        for (NoteEvent event : noteEvents) {
            noteList.add(event.serializeNBT());
        }
        tag.put(KEY_NOTES, noteList);
    }

    public short endTime() {
        return (short) (time + length - 1);
    }

    public short startTime() {
        return time;
    }

    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putByte("n", note);
        tag.putShort("d", time);
        tag.putByte("v", volume);
        tag.putByte("l", length);
        return tag;
    }

    public void deserializeNBT(CompoundTag tag) {
        this.note = tag.getByte("n");
        this.time = tag.getShort("d");
        this.volume = tag.getByte("v");
        this.length = tag.getByte("l");
    }

    public void encodeToBuffer(FriendlyByteBuf buf) {
        buf.writeByte(note);
        buf.writeShort(time);
        buf.writeByte(volume);
        buf.writeByte(length);
    }

    public void decodeFromBuffer(FriendlyByteBuf buf) {
        this.note = buf.readByte();
        this.time = buf.readShort();
        this.volume = buf.readByte();
        this.length = buf.readByte();
    }

    public float floatVolume() {
        return volume / 127.0f;
    }
}
