package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;

@SuppressWarnings("unused")
public record ExportMusicPacket(String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ExportMusicPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("export_music"));
    public static final StreamCodec<FriendlyByteBuf, ExportMusicPacket> PACKET_CODEC = StreamCodec.ofMember(ExportMusicPacket::encode, ExportMusicPacket::decode);

    public static ExportMusicPacket decode(FriendlyByteBuf buf) {
        try {
            String name = buf.readUtf(64);
            return new ExportMusicPacket(name);
        } catch (IndexOutOfBoundsException ioe) {
            System.err.println("Exception while reading ExportMusicPacket: " + ioe);
            return null;
        }
    }

    public FriendlyByteBuf encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
        return buf;
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
