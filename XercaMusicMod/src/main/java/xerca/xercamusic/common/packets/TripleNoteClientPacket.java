package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

import java.util.Objects;

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

    public static TripleNoteClientPacket decode(FriendlyByteBuf buf) {
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

            result.entity = Objects.requireNonNull(Minecraft.getInstance().level).getEntity(entityId);
            result.instrumentItem = Items.instruments[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(TripleNoteClientPacket pkt, FriendlyByteBuf buf) {

        int instrumentId = pkt.getInstrumentItem().getInstrumentId();
        int entityId = pkt.getEntity().getId();

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

    public int getNote2() {
        return note2;
    }

    public int getNote3() {
        return note3;
    }

    public ItemInstrument getInstrumentItem() {
        return instrumentItem;
    }

    public Entity getEntity() {
        return entity;
    }

    public void setEntity(Entity entity) {
        this.entity = entity;
    }


}
