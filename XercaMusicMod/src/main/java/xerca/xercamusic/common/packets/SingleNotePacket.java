package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

public class SingleNotePacket {
    private int note;
    private IItemInstrument instrumentItem;
    private boolean isStop;
    private float volume;
    private boolean messageIsValid;

    public SingleNotePacket(int note, IItemInstrument itemInstrument, boolean isStop, float volume) {
        this.note = note;
        this.instrumentItem = itemInstrument;
        this.isStop = isStop;
        this.volume = volume;
    }
    public SingleNotePacket(int note, IItemInstrument itemInstrument, boolean isStop) {
        this(note, itemInstrument, isStop, 1f);
    }

    public SingleNotePacket() {
        this.messageIsValid = false;
    }

    public static SingleNotePacket decode(FriendlyByteBuf buf) {
        SingleNotePacket result = new SingleNotePacket();
        try {
            result.note = buf.readInt();
            int instrumentId = buf.readInt();
            result.isStop = buf.readBoolean();
            result.volume = buf.readFloat();
            if(instrumentId < 0 || instrumentId >= Items.instruments.length){
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }
            result.instrumentItem = Items.instruments[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(SingleNotePacket pkt, FriendlyByteBuf buf) {
        buf.writeInt(pkt.note);
        buf.writeInt(pkt.instrumentItem.getInstrumentId());
        buf.writeBoolean(pkt.isStop());
        buf.writeFloat(pkt.getVolume());
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }

    public int getNote() {
        return note;
    }

    public void setNote(int note) {
        this.note = note;
    }

    public IItemInstrument getInstrumentItem() {
        return instrumentItem;
    }

    public boolean isStop() {
        return isStop;
    }

    public float getVolume() {
        return volume;
    }
}
