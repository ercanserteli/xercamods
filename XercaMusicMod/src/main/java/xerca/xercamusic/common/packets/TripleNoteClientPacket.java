package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

public class TripleNoteClientPacket {
    private int note1;
    private int note2;
    private int note3;
    private IItemInstrument instrumentItem;
    private int entityId;
    private boolean messageIsValid;

    public TripleNoteClientPacket(int note1, int note2, int note3, IItemInstrument itemInstrument, Entity entity) {
        this.note1 = note1;
        this.note2 = note2;
        this.note3 = note3;
        this.instrumentItem = itemInstrument;
        this.entityId = entity.getId();
    }

    public TripleNoteClientPacket() {
        this.messageIsValid = false;
    }

    public static TripleNoteClientPacket decode(FriendlyByteBuf buf) {
        TripleNoteClientPacket result = new TripleNoteClientPacket();
        try {
            result.note1 = buf.readInt();
            result.note2 = buf.readInt();
            result.note3 = buf.readInt();
            int instrumentId = buf.readInt();
            int entityId = buf.readInt();

            if (instrumentId < 0 || instrumentId >= Items.instruments.length) {
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            result.entityId = entityId;
            result.instrumentItem = Items.instruments[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket: {}", ioe.toString());
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(TripleNoteClientPacket pkt, FriendlyByteBuf buf) {
        int instrumentId = pkt.getInstrumentItem().getInstrumentId();

        buf.writeInt(pkt.getNote1());
        buf.writeInt(pkt.getNote2());
        buf.writeInt(pkt.getNote3());
        buf.writeInt(instrumentId);
        buf.writeInt(pkt.getEntityId());
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }


    public int getNote1() {
        return note1;
    }

    public int getNote2() {
        return note2;
    }

    public int getNote3() {
        return note3;
    }

    public IItemInstrument getInstrumentItem() {
        return instrumentItem;
    }

    public int getEntityId() {
        return entityId;
    }


}
