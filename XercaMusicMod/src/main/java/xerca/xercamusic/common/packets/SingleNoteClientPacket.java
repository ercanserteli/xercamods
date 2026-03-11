package xerca.xercamusic.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

public class SingleNoteClientPacket {
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
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket: {}", ioe.toString());
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(SingleNoteClientPacket pkt, FriendlyByteBuf buf) {
        int instrumentId = pkt.getInstrumentItem().getInstrumentId();

        buf.writeInt(pkt.getNote());
        buf.writeInt(instrumentId);
        buf.writeInt(pkt.getPlayerId());
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

    public int getPlayerId() {
        return playerId;
    }

    public boolean isStop() {
        return isStop;
    }


    public float getVolume() {
        return volume;
    }
}
