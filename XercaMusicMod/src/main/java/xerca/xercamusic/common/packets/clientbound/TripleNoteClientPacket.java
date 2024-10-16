package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;


public record TripleNoteClientPacket(int note1, int note2, int note3, IItemInstrument instrumentItem, Entity entity) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TripleNoteClientPacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "triple_note_client"));
    public static final StreamCodec<FriendlyByteBuf, TripleNoteClientPacket> PACKET_CODEC = StreamCodec.ofMember(TripleNoteClientPacket::encode, TripleNoteClientPacket::decode);

    public static TripleNoteClientPacket decode(FriendlyByteBuf buf) {
        try {
            int note1 = buf.readInt();
            int note2 = buf.readInt();
            int note3 = buf.readInt();
            int instrumentId = buf.readInt();
            int entityId = buf.readInt();

            if(instrumentId < 0 || instrumentId >= Items.instruments.length){
                throw new IndexOutOfBoundsException("Invalid instrumentId: " + instrumentId);
            }

            ClientLevel level = Minecraft.getInstance().level;
            if(level == null) {
                return null;
            }
            Entity entity = level.getEntity(entityId);
            IItemInstrument instrumentItem = Items.instruments[instrumentId];
            return new TripleNoteClientPacket(note1, note2, note3, instrumentItem, entity);
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading SingleNotePacket:", ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        int instrumentId = instrumentItem.getInstrumentId();
        int entityId = entity.getId();

        buf.writeInt(note1);
        buf.writeInt(note2);
        buf.writeInt(note3);
        buf.writeInt(instrumentId);
        buf.writeInt(entityId);
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
