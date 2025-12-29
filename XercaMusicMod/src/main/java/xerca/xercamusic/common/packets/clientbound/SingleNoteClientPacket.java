package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;


public record SingleNoteClientPacket(int note, IItemInstrument instrumentItem, int playerId, boolean isStop,
                                     float volume) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SingleNoteClientPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("single_note_client"));
    public static final StreamCodec<FriendlyByteBuf, SingleNoteClientPacket> PACKET_CODEC = StreamCodec.ofMember(SingleNoteClientPacket::encode, SingleNoteClientPacket::decode);

    public SingleNoteClientPacket(int note, IItemInstrument instrumentItem, Player playerEntity, boolean isStop, float volume) {
        this(note, instrumentItem, playerEntity.getId(), isStop, volume);
    }

    public static SingleNoteClientPacket decode(FriendlyByteBuf buf) {
        int note = buf.readInt();
        int instrumentId = buf.readInt();
        int playerId = buf.readInt();
        boolean isStop = buf.readBoolean();
        float volume = buf.readFloat();

        if (instrumentId < 0 || instrumentId >= Items.INSTRUMENTS.size()) {
            Mod.LOGGER.warn("Invalid instrumentId: {}", instrumentId);
            instrumentId = 0;
        }

        IItemInstrument instrumentItem = Items.INSTRUMENTS.get(instrumentId);
        return new SingleNoteClientPacket(note, instrumentItem, playerId, isStop, volume);
    }

    public void encode(FriendlyByteBuf buf) {
        int instrumentId = instrumentItem.getInstrumentId();

        buf.writeInt(note);
        buf.writeInt(instrumentId);
        buf.writeInt(playerId);
        buf.writeBoolean(isStop);
        buf.writeFloat(volume);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
