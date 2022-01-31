package xerca.xercamusic.common.packets;

import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

public class SingleNotePacket {
    private int note;
    private ItemInstrument instrumentItem;
    private boolean isStop;
    private boolean messageIsValid;

    public SingleNotePacket(int note, ItemInstrument itemInstrument, boolean isStop) {
        this.note = note;
        this.instrumentItem = itemInstrument;
        this.isStop = isStop;
    }

    public SingleNotePacket() {
        this.messageIsValid = false;
    }

    public static SingleNotePacket decode(PacketBuffer buf) {
        SingleNotePacket result = new SingleNotePacket();
        try {
            result.note = buf.readInt();
            int instrumentId = buf.readInt();
            result.isStop = buf.readBoolean();
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

    public static void encode(SingleNotePacket pkt, PacketBuffer buf) {
        buf.writeInt(pkt.note);
        buf.writeInt(pkt.instrumentItem.getInstrumentId());
        buf.writeBoolean(pkt.isStop());
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

    public ItemInstrument getInstrumentItem() {
        return instrumentItem;
    }

    public void setInstrumentItem(ItemInstrument instrumentItem) {
        this.instrumentItem = instrumentItem;
    }

    public boolean isStop() {
        return isStop;
    }

    public void setStop(boolean stop) {
        isStop = stop;
    }
}
