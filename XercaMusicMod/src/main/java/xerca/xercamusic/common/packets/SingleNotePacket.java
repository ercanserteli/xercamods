package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

public class SingleNotePacket {
    private int note;
    private ItemInstrument instrumentItem;
    private boolean messageIsValid;

    public SingleNotePacket(int note, ItemInstrument itemInstrument) {
        this.note = note;
        this.instrumentItem = itemInstrument;
    }

    public SingleNotePacket() {
        this.messageIsValid = false;
    }

    public static SingleNotePacket decode(FriendlyByteBuf buf) {
        SingleNotePacket result = new SingleNotePacket();
        try {
            result.note = buf.readInt();
            int instrumentId = buf.readInt();
            if(result.note < 0 || result.note >= 48){
                throw new IndexOutOfBoundsException("Invalid note: " + result.note);
            }
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
}
