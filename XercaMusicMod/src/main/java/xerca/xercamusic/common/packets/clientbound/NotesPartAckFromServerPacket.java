package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;

import java.util.UUID;

public record NotesPartAckFromServerPacket(UUID id) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<NotesPartAckFromServerPacket> PACKET_ID = new CustomPacketPayload.Type<>(new ResourceLocation(Mod.MODID, "notes_part_ack_from_server"));
    public static final StreamCodec<FriendlyByteBuf, NotesPartAckFromServerPacket> PACKET_CODEC = StreamCodec.ofMember(NotesPartAckFromServerPacket::encode, NotesPartAckFromServerPacket::decode);

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUUID(id);
        return buf;
    }

    public static NotesPartAckFromServerPacket decode(FriendlyByteBuf buf) {
        try {
            UUID id = buf.readUUID();
            return new NotesPartAckFromServerPacket(id);
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading NotesPartAckFromServerPacket:", ioe);
            return null;
        }
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}

