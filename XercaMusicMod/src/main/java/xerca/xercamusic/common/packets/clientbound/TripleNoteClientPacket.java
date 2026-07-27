package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.IPacket;

public class TripleNoteClientPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(Mod.MODID, "triple_note_client");
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

            if (instrumentId < 0 || instrumentId >= Items.INSTRUMENTS.length) {
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            result.entityId = entityId;
            result.instrumentItem = Items.INSTRUMENTS[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading SingleNotePacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        int instrumentId = getInstrumentItem().getInstrumentId();

        buf.writeInt(getNote1());
        buf.writeInt(getNote2());
        buf.writeInt(getNote3());
        buf.writeInt(instrumentId);
        buf.writeInt(getEntityId());
        return buf;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }


    public int getNote1() {
        return note1;
    }

    @SuppressWarnings("unused")
    public void setNote1(int note) {
        this.note1 = note;
    }

    public int getNote2() {
        return note2;
    }

    @SuppressWarnings("unused")
    public void setNote2(int note) {
        this.note2 = note;
    }

    public int getNote3() {
        return note3;
    }

    @SuppressWarnings("unused")
    public void setNote3(int note) {
        this.note3 = note;
    }

    public IItemInstrument getInstrumentItem() {
        return instrumentItem;
    }

    @SuppressWarnings("unused")
    public void setInstrumentItem(IItemInstrument instrumentItem) {
        this.instrumentItem = instrumentItem;
    }

    public int getEntityId() {
        return entityId;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
