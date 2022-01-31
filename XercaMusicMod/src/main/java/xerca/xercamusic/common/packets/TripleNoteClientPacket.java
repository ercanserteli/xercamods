package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

public class TripleNoteClientPacket {
    private int note1;
    private int note2;
    private int note3;
    private ItemInstrument instrumentItem;
    private Entity entity;
    private boolean messageIsValid;

    public TripleNoteClientPacket(int note1, int note2, int note3, ItemInstrument itemInstrument, Entity entity) {
        this.note1 = note1;
        this.note2 = note2;
        this.note3 = note3;
        this.instrumentItem = itemInstrument;
        this.entity = entity;
    }

    public TripleNoteClientPacket() {
        this.messageIsValid = false;
    }

    public static TripleNoteClientPacket decode(PacketBuffer buf) {
    	Minecraft mc = Minecraft.getInstance();
        TripleNoteClientPacket result = new TripleNoteClientPacket();
        try {
            result.note1 = buf.readInt();
            result.note2 = buf.readInt();
            result.note3 = buf.readInt();
            int instrumentId = buf.readInt();
            int entityId = buf.readInt();

            if(instrumentId < 0 || instrumentId >= Items.instruments.length){
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            result.entity = mc.world.getEntityByID(entityId);
            result.instrumentItem = Items.instruments[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(TripleNoteClientPacket pkt, PacketBuffer buf) {

        int instrumentId = pkt.getInstrumentItem().getInstrumentId();
        int entityId = pkt.getEntity().getEntityId();

        buf.writeInt(pkt.getNote1());
        buf.writeInt(pkt.getNote2());
        buf.writeInt(pkt.getNote3());
        buf.writeInt(instrumentId);
        buf.writeInt(entityId);
    }

    public boolean isMessageValid() {
        return messageIsValid;
    }


    public int getNote1() {
        return note1;
    }

    public void setNote1(int note) {
        this.note1 = note;
    }

    public int getNote2() {
        return note2;
    }

    public void setNote2(int note) {
        this.note2 = note;
    }

    public int getNote3() {
        return note3;
    }

    public void setNote3(int note) {
        this.note3 = note;
    }

    public ItemInstrument getInstrumentItem() {
        return instrumentItem;
    }

    public void setInstrumentItem(ItemInstrument instrumentItem) {
        this.instrumentItem = instrumentItem;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }


}
