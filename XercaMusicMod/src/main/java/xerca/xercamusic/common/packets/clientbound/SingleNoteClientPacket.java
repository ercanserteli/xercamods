package xerca.xercamusic.common.packets.clientbound;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.IPacket;

public class SingleNoteClientPacket implements IPacket {
    private int note;
    private IItemInstrument instrumentItem;
    private int playerId;
    private boolean isStop;
    private float volume;
    private boolean messageIsValid;

    public SingleNoteClientPacket(int note, IItemInstrument itemInstrument, Player playerEntity, boolean isStop, float volume) {
        this.note = note;
        this.instrumentItem = itemInstrument;
        this.playerId = playerEntity.getId();
        this.isStop = isStop;
        this.volume = volume;
    }

    public SingleNoteClientPacket() {
        this.messageIsValid = false;
    }

    public static SingleNoteClientPacket decode(FriendlyByteBuf buf) {
        SingleNoteClientPacket result = new SingleNoteClientPacket();
        try {
            result.note = buf.readInt();
            int instrumentId = buf.readInt();
            int playerId = buf.readInt();
            result.isStop = buf.readBoolean();
            result.volume = buf.readFloat();

            if (instrumentId < 0 || instrumentId >= Items.instruments.length) {
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            result.playerId = playerId;
            result.instrumentItem = Items.instruments[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket", ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public FriendlyByteBuf encode() {
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());

        int instrumentId = getInstrumentItem().getInstrumentId();

        buf.writeInt(getNote());
        buf.writeInt(instrumentId);
        buf.writeInt(getPlayerId());
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

    public int getPlayerId() {
        return playerId;
    }

    public boolean isStop() {
        return isStop;
    }

    @SuppressWarnings("unused")
    public void setStop(boolean stop) {
        isStop = stop;
    }
    public float getVolume() {
        return volume;
    }
}
