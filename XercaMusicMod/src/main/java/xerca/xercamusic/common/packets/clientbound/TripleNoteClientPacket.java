package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;
import xerca.xercamusic.common.item.IItemInstrument;
import xerca.xercamusic.common.item.Items;


public record TripleNoteClientPacket(int note1, int note2, int note3, IItemInstrument instrumentItem, int entityId) implements CustomPacketPayload {
    public TripleNoteClientPacket(int note1, int note2, int note3, IItemInstrument instrumentItem, Entity entity) {
        this(note1, note2, note3, instrumentItem, entity.getId());
    }

    public static final CustomPacketPayload.Type<TripleNoteClientPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("triple_note_client"));
    public static final StreamCodec<FriendlyByteBuf, TripleNoteClientPacket> PACKET_CODEC = StreamCodec.ofMember(TripleNoteClientPacket::encode, TripleNoteClientPacket::decode);

    public static TripleNoteClientPacket decode(FriendlyByteBuf buf) {
        int note1 = buf.readInt();
        int note2 = buf.readInt();
        int note3 = buf.readInt();
        int instrumentId = buf.readInt();
        int entityId = buf.readInt();

        if(instrumentId < 0 || instrumentId >= Items.instruments.length){
            Mod.LOGGER.warn("Invalid instrumentId: {}", instrumentId);
            instrumentId = 0;
        }

        IItemInstrument instrumentItem = Items.instruments[instrumentId];
        return new TripleNoteClientPacket(note1, note2, note3, instrumentItem, entityId);
    }

    public Entity entity() {
        ClientLevel level = Minecraft.getInstance().level;
        if(level == null) {
            Mod.LOGGER.warn("Level is null while trying to get entity");
            return null;
        }

        Entity entity = level.getEntity(entityId);
        if(entity == null){
            Mod.LOGGER.warn("Invalid entityId: {}", entityId);
            return Minecraft.getInstance().player;
        }
        return entity;
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        int instrumentId = instrumentItem.getInstrumentId();

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
