package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;

public record SingleNotePacket(int note, IItemInstrument instrumentItem, boolean isStop, float volume) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SingleNotePacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("single_note"));
    public static final StreamCodec<FriendlyByteBuf, SingleNotePacket> PACKET_CODEC = StreamCodec.ofMember(SingleNotePacket::encode, SingleNotePacket::decode);

    public static SingleNotePacket decode(FriendlyByteBuf buf) {
        try {
            int note = buf.readInt();
            int instrumentId = buf.readInt();
            boolean isStop = buf.readBoolean();
            float volume = buf.readFloat();
            if(instrumentId < 0 || instrumentId >= Items.instruments.length){
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }
            IItemInstrument instrumentItem = Items.instruments[instrumentId];
            return new SingleNotePacket(note, instrumentItem, isStop, volume);
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading SingleNotePacket:", ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(note);
        buf.writeInt(instrumentItem.getInstrumentId());
        buf.writeBoolean(isStop);
        buf.writeFloat(volume);
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
