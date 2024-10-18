package xerca.xercamusic.common.packets.serverbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;


public record MusicEndedPacket(int playerId) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<MusicEndedPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("music_ended"));
    public static final StreamCodec<FriendlyByteBuf, MusicEndedPacket> PACKET_CODEC = StreamCodec.ofMember(MusicEndedPacket::encode, MusicEndedPacket::decode);

    public static MusicEndedPacket decode(FriendlyByteBuf buf) {
        try {
            int playerId = buf.readInt();
            return new MusicEndedPacket(playerId);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading MusicEndedPacket: " + ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeInt(playerId);
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
