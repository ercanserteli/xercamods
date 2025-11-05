package xerca.xercamusic.common.packets.clientbound;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import org.jetbrains.annotations.NotNull;
import xerca.xercamusic.common.Mod;


public record ImportMusicPacket(String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<ImportMusicPacket> PACKET_ID = new CustomPacketPayload.Type<>(Mod.id("import_music"));
    public static final StreamCodec<FriendlyByteBuf, ImportMusicPacket> PACKET_CODEC = StreamCodec.ofMember(ImportMusicPacket::encode, ImportMusicPacket::decode);

    public static ImportMusicPacket decode(FriendlyByteBuf buf) {
        String name = buf.readUtf(64);
        return new ImportMusicPacket(name);
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(name);
    }

    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return PACKET_ID;
    }
}
