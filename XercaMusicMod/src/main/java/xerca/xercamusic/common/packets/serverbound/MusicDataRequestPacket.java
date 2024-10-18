package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;

import java.util.UUID;

public record MusicDataRequestPacket(UUID id, int version) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MusicDataRequestPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("music_data_request"));
    public static final StreamCodec<FriendlyByteBuf, MusicDataRequestPacket> PACKET_CODEC = StreamCodec.ofMember(MusicDataRequestPacket::encode, MusicDataRequestPacket::decode);

    public static MusicDataRequestPacket decode(FriendlyByteBuf buf) {
        try {
            UUID id = buf.readUUID();
            int version = buf.readInt();
            return new MusicDataRequestPacket(id, version);
        } catch (IndexOutOfBoundsException ioe) {
            Mod.LOGGER.error("Exception while reading MusicDataRequestPacket:", ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUUID(id());
        buf.writeInt(version());
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
