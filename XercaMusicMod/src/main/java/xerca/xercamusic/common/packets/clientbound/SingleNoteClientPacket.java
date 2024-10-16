package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;


public record SingleNoteClientPacket(int note, IItemInstrument instrumentItem, Player playerEntity, boolean isStop, float volume) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<SingleNoteClientPacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "single_note_client"));
    public static final StreamCodec<FriendlyByteBuf, SingleNoteClientPacket> PACKET_CODEC = StreamCodec.ofMember(SingleNoteClientPacket::encode, SingleNoteClientPacket::decode);

    public static SingleNoteClientPacket decode(FriendlyByteBuf buf) {
        try {
            int note = buf.readInt();
            int instrumentId = buf.readInt();
            int playerId = buf.readInt();
            boolean isStop = buf.readBoolean();
            float volume = buf.readFloat();

            if(instrumentId < 0 || instrumentId >= Items.instruments.length){
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) {
                return null;
            }
            Entity entity = level.getEntity(playerId);
            if(!(entity instanceof Player playerEntity)){
                throw new IndexOutOfBoundsException("Invalid playerId: " + playerId);
            }

            IItemInstrument instrumentItem = Items.instruments[instrumentId];
            return new SingleNoteClientPacket(note, instrumentItem, playerEntity, isStop, volume);
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading SingleNotePacket:", ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        int instrumentId = instrumentItem.getInstrumentId();
        int playerId = playerEntity.getId();

        buf.writeInt(note);
        buf.writeInt(instrumentId);
        buf.writeInt(playerId);
        buf.writeBoolean(isStop);
        buf.writeFloat(volume);
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
