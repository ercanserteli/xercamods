package xerca.xercamusic.common.packets;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.ItemInstrument;
import xerca.xercamusic.common.item.Items;

public class SingleNoteClientPacket {
    private int note;
    private ItemInstrument instrumentItem;
    private PlayerEntity playerEntity;
    private boolean messageIsValid;

    public SingleNoteClientPacket(int note, ItemInstrument itemInstrument, PlayerEntity playerEntity) {
        this.note = note;
        this.instrumentItem = itemInstrument;
        this.playerEntity = playerEntity;
    }

    public SingleNoteClientPacket() {
        this.messageIsValid = false;
    }

    public static SingleNoteClientPacket decode(PacketBuffer buf) {
        SingleNoteClientPacket result = new SingleNoteClientPacket();
        try {
            result.note = buf.readInt();
            int instrumentId = buf.readInt();
            int playerId = buf.readInt();

            if(result.note < 0 || result.note >= 48){
                throw new IndexOutOfBoundsException("Invalid note: " + result.note);
            }
            if(instrumentId < 0 || instrumentId >= Items.instruments.length){
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            Entity entity = Minecraft.getInstance().world.getEntityByID(playerId);
            if(!(entity instanceof PlayerEntity)){
                throw new IndexOutOfBoundsException("Invalid playerId: " + playerId);
            }

            result.playerEntity = (PlayerEntity) entity;
            result.instrumentItem = Items.instruments[instrumentId];
        } catch (IndexOutOfBoundsException ioe) {
            XercaMusic.LOGGER.error("Exception while reading SingleNotePacket: " + ioe);
            return null;
        }
        result.messageIsValid = true;
        return result;
    }

    public static void encode(SingleNoteClientPacket pkt, PacketBuffer buf) {

        int instrumentId = pkt.getInstrumentItem().getInstrumentId();
        int playerId = pkt.getPlayerEntity().getEntityId();

        buf.writeInt(pkt.getNote());
        buf.writeInt(instrumentId);
        buf.writeInt(playerId);
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

    public PlayerEntity getPlayerEntity() {
        return playerEntity;
    }

    public void setPlayerEntity(PlayerEntity playerEntity) {
        this.playerEntity = playerEntity;
    }


}
