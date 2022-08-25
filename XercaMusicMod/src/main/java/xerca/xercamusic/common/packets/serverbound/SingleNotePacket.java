package xerca.xercamusic.common.packets.serverbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.IPacket;

public class SingleNotePacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "single_note");
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

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(note);
        buf.writeInt(instrumentItem.getInstrumentId());
        buf.writeBoolean(isStop());
        buf.writeFloat(getVolume());
        return buf;
    }

    @SuppressWarnings("unused")
    public boolean isMessageValid() {
        return messageIsValid;
    }

    public int getNote() {
        return note;
    }

    @SuppressWarnings("unused")
    public void setNote(int note) {
        this.note = note;
    }

    public IItemInstrument getInstrumentItem() {
        return instrumentItem;
    }

    @SuppressWarnings("unused")
    public void setInstrumentItem(IItemInstrument instrumentItem) {
        this.instrumentItem = instrumentItem;
    }

    public boolean isStop() {
        return isStop;
    }

    @SuppressWarnings("unused")
    public void setStop(boolean stop) {
        isStop = stop;
    }

    @Override
    public ResourceLocation getID() {
        return ID;
    }

    public float getVolume() {
        return volume;
    }
}
