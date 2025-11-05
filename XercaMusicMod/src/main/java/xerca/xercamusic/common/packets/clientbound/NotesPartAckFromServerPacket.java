package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;

import java.util.UUID;

public record NotesPartAckFromServerPacket(UUID id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<NotesPartAckFromServerPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("notes_part_ack_from_server"));
    public static final StreamCodec<FriendlyByteBuf, NotesPartAckFromServerPacket> PACKET_CODEC = StreamCodec.ofMember(NotesPartAckFromServerPacket::encode, NotesPartAckFromServerPacket::decode);

    public static NotesPartAckFromServerPacket decode(FriendlyByteBuf buf) {
        UUID id = buf.readUUID();
        return new NotesPartAckFromServerPacket(id);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUUID(id);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

