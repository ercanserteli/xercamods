package xerca.xercamusic.common.packets.clientbound;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import xerca.xercamusic.common.XercaMusic;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;
import xerca.xercamusic.common.packets.IPacket;

public class SingleNoteClientPacket implements IPacket {
    public static final ResourceLocation ID = new ResourceLocation(XercaMusic.MODID, "single_note_client");
    private int note;
    private IItemInstrument instrumentItem;
    private Player playerEntity;
    private boolean isStop;
    private float volume;
    private boolean messageIsValid;

    public SingleNoteClientPacket(int note, IItemInstrument itemInstrument, Player playerEntity, boolean isStop, float volume) {
        this.note = note;
        this.instrumentItem = itemInstrument;
        this.playerEntity = playerEntity;
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

            if(instrumentId < 0 || instrumentId >= Items.instruments.length){
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) {
                return null;
            }
            Entity entity = level.getEntity(playerId);
            if(!(entity instanceof Player)){
                throw new IndexOutOfBoundsException("Invalid playerId: " + playerId);
            }

            result.playerEntity = (Player) entity;
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

        int instrumentId = getInstrumentItem().getInstrumentId();
        int playerId = getPlayerEntity().getId();

        buf.writeInt(getNote());
        buf.writeInt(instrumentId);
        buf.writeInt(playerId);
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

    public Player getPlayerEntity() {
        return playerEntity;
    }

    @SuppressWarnings("unused")
    public void setPlayerEntity(Player playerEntity) {
        this.playerEntity = playerEntity;
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
