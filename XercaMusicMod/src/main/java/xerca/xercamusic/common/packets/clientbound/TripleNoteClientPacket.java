package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.IPacket;

public class TripleNoteClientPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "triple_note_client");
    private int note1;
    private int note2;
    private int note3;
    private IItemInstrument instrumentItem;
    private Entity entity;
    private boolean messageIsValid;

    public TripleNoteClientPacket(int note1, int note2, int note3, IItemInstrument itemInstrument, Entity entity) {
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

            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) {
                return null;
            }
            result.entity = level.getEntity(entityId);
            result.instrumentItem = Items.instruments[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket:", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();

        int instrumentId = getInstrumentItem().getInstrumentId();
        int entityId = getEntity().getId();

        buf.writeInt(getNote1());
        buf.writeInt(getNote2());
        buf.writeInt(getNote3());
        buf.writeInt(instrumentId);
        buf.writeInt(entityId);
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

    public Entity getEntity() {
        return entity;
    }

    @SuppressWarnings("unused")
    public void setEntity(Entity entity) {
        this.entity = entity;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }
}
