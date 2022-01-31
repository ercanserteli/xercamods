package xerca.xercamusic.common;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.common.util.Constants.NBT;
import net.minecraftforge.common.util.INBTSerializable;

import java.util.ArrayList;

public class NoteEvent implements INBTSerializable<CompoundNBT> {
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

    public short endTime(){
        return (short)(time + length - 1);
    }

    public short startTime(){
        return time;
    }

    @Override
    public CompoundNBT serializeNBT() {
        CompoundNBT tag = new CompoundNBT();
        tag.putByte("n", note);
        tag.putShort("d", time);
        tag.putByte("v", volume);
        tag.putByte("l", length);
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundNBT tag) {
        this.note = tag.getByte("n");
        this.time = tag.getShort("d");
        this.volume = tag.getByte("v");
        this.length = tag.getByte("l");
    }

    public static NoteEvent fromNBT(CompoundNBT tag){
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.deserializeNBT(tag);
        return noteEvent;
    }

    public void encodeToBuffer(PacketBuffer buf){
        buf.writeByte(note);
        buf.writeShort(time);
        buf.writeByte(volume);
        buf.writeByte(length);
    }

    public void decodeFromBuffer(PacketBuffer buf){
        this.note = buf.readByte();
        this.time = buf.readShort();
        this.volume = buf.readByte();
        this.length = buf.readByte();
    }

    public static NoteEvent fromBuffer(PacketBuffer buf){
        NoteEvent noteEvent = new NoteEvent();
        noteEvent.decodeFromBuffer(buf);
        return noteEvent;
    }

    public static void fillArrayFromNBT(ArrayList<NoteEvent> noteEvents, CompoundNBT tag) {
        ListNBT notesTag = tag.getList("notes", NBT.TAG_COMPOUND);
        for(int i=0; i<notesTag.size(); i++){
            noteEvents.add(NoteEvent.fromNBT(notesTag.getCompound(i)));
        }
    }

    public static void fillNBTFromArray(ArrayList<NoteEvent> noteEvents, CompoundNBT tag) {
        ListNBT noteList = new ListNBT();
        for(NoteEvent event : noteEvents){
            noteList.add(event.serializeNBT());
        }
        tag.put("notes", noteList);
    }

    public float floatVolume() {
        return ((float)volume)/127.0f;
    }

    @Override
    public NoteEvent clone() {
        return new NoteEvent(note, time, volume, length);
    }
}
